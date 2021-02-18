/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ipn.cic.piig.daa;

import java.util.*;
import org.graphstream.algorithm.generator.*;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;

/**
 *
 * @author rolando
 */
public class DAA {
    public static final boolean DIRECTED_GRAPH = true;
    public static final boolean UNDIRECTED_GRAPH = false;
    public static final boolean CYCLES_ALLOWED = true;
    public static final boolean CYCLES_NOT_ALLOWED = false;
    
    protected static String WEIGHT_PARAM = "__weight";
    protected static String EDGES_DATA = "__edges";
    protected static String NODE_NAME_PREFIX = "nodo_";
    protected static String KRUSKAL_CUT = "__kruskalCut";
    protected static String DIJKSTRA_LENGTH_FROM_SEED = "__dijkstraLenFrom_";
    protected static String DIJKSTRA_PREV_NODE_FOR_SEED = "__dijkstraPrevNode_";
    protected static String DIJKSTRA_PREV_EDGE_FOR_SEED = "__dijkstraPrevEdge_";
    
    public static String GENERATOR_ATTR = "__generator__";
    public static final int GENERATOR_ERDOS = 0;
    public static final int GENERATOR_GILBERT = 1;
    public static final int GENERATOR_GEO = 2;
    public static final int GENERATOR_BARABASI = 3;
    public static final int GENERATOR_GRID = 4;
    public static final int GENERATOR_DOROGOVTSEV = 5;
    public static final int GENERATOR_BARABASI_GS = 6;
    public static final int GENERATOR_SMALL_WORLD = 7;
    public static final int GENERATOR_EUCLIDEAN = 8;
        
    ////////////////////////////////////////////////////////////////////////////
    // METODOS AUXILIARES
    
    /**
     * Pone en espera el hilo actual
     * @param ms tiempo de espera en milisegundos
     */
    public static void pause(int ms) { 
        try { 
            Thread.sleep(ms); 
        } catch(Exception e) {}
    }
    
    /**
     * Genera una cadena rgb con un color aleatorio dentro del rango especificado
     * @param min intensidad mínima
     * @param max intensidad máxima
     * @return cadena con el color generado
     */
    public static String randomColor(int min, int max) { 
        min = min < 0 ? 0 : min;
        max = max > 255 ? 255 : max;
        
        if( min > max) {
            int t = min;
            min = max;
            max = t;
        }
        
        max = max - min;
        
        int r =(int)(min + Math.random() * max);
        int g =(int)(min + Math.random() * max);
        int b =(int)(min + Math.random() * max);
        
        return "rgb(" + r +","+ g +","+ b +");"; 
    }
    
    /**
     * Genera una cadena con un tipo aleatorio de forma de un nodo, dentro de un
     * conjunto de formas posibles
     * @return cadena con la forma generada
     */
    public static String randomShape() {
        String shapes[] = {"circle", "box", "rounded-box", "diamond", "cross", "triangle" };
        
        return shapes[(int)(Math.random()*shapes.length)] + ";";
    }
    
    /**
     * Asigna la propiedad weight a todas las aristas con un valor aleatorio entre min y max.
     * @param min Valor mínimo
     * @param max Valor máximo
     */
    public static void genRandomEdgeWeights(Graph g, double min, double max) {
        g.edges().forEach( e -> {
            e.setAttribute(WEIGHT_PARAM, min + Math.random()*(max-min));
        });
    }
    
    /**
     * Genera un número entero aleatorio en un rango dado.
     * @param m Valor mínimo
     * @param M Valor máximo
     * @return Un número aleatorio entre el rango dado
     */
    public static int randomInt(int m, int M) {
        return m + (int)((M-m)*Math.random());
    }
    
    /**
     * Modelo Geográfico - Calcula la distancia euclideana entre dos nodos
     * @param u Nodo fuente
     * @param v Nodo destino
     * @return 
     */
    public static double dist(Node u, Node v) {
        double ux = (double)u.getAttribute("x");
        double uy = (double)u.getAttribute("y");
        double vx = (double)v.getAttribute("x");
        double vy = (double)v.getAttribute("y");
        
        return Math.sqrt(sqr(ux-vx)+sqr(uy-vy));
    }
    
    /**
     * Calcular el cuadrado de un número
     * @param x El número
     * @return El cuadrado del número
     */
    public static double sqr(double x) {
        return x*x;
    }
    
    /**
     * Genera un arreglo de tamaño size, con números entre 0 y size; ordenados al azar.
     * @param size Tamaño del arreglo
     * @return El arreglo de números ordenados al azar
     */
    public static int[] randomArray(int size){
        int k=size;
        int[] numeros=new int[size];
        int[] resultado=new int[size];
        Random rnd=new Random();
        int res;
        
        for(int i=0;i<size;i++) {
            numeros[i]=i;
        }
        
        for(int i=0;i<size;i++) {
            res=rnd.nextInt(k);            
            resultado[i]=numeros[res];
            numeros[res]=numeros[k-1];
            k--;
            
        }
        
        return resultado;
    }
    
    /**
     * Dar formato a un nodo
     * @param n nodoo a formatear
     */
    static void format(Node n) {
        String termTextColor = "rgb( 120, 50,50);";
        String termFillColor = "rgb(250,250,250);";
        String termStrokeColor = termTextColor;
        
        termFillColor = termTextColor = DAA.randomColor(100, 255);
        
        
        //n.setAttribute("ui.label", n.getId());
//        n.setAttribute("ui.style", "text-size:12;");           
//        n.setAttribute("ui.style", "text-color:"+ termTextColor);
//        n.setAttribute("ui.style", "size:10;");    
        n.setAttribute("ui.style", "fill-color:"+ termFillColor);
//        n.setAttribute("ui.style", "stroke-mode:plain;");
//        n.setAttribute("ui.style", "stroke-color:black;");
//        n.setAttribute("ui.style", "shape:rounded-box;");
//        n.setAttribute("ui.style", "size-mode:fit;");
    }
    
    /**
     * Agregar una arista al grafo
     * @param g el grafo
     * @param n1 nodo de origen
     * @param n2 nodo de destino
     * @param sleep tiempo de espera de la animación
     * @return la arista añadida
     * @throws Exception si falla la espera
     */
    static Edge addEdge(Graph g, String n1, String n2, int sleep) {
        Edge e = g.addEdge(n1 +"--"+ n2, n1, n2);
                
        DAA.pause(sleep);
        
        e.setAttribute("ui.style", "fill-color:gray;");
//        e.setAttribute("ui.style", "stroke-mode:dots;");
//        e.setAttribute("ui.style", "text-color:gray;");
        
//        g.getNode(n1).setAttribute("ui.style", "fill-color:black");
//        g.getNode(n2).setAttribute("ui.style", "fill-color:black");
        
        return e;
    }
    
    /**
     * Da formato a una cadena para especificar un color dados sus componentes 
     * primarios
     * @param r rojo (0-255)
     * @param g verde (0-255)
     * @param b azul (0-255)
     * @return la cadena con el color
     */
    static String rgb(int r, int g, int b) {
        return "rgb("+ r +","+ g +","+ b +");";
    }
    
    /**
     * Da formato a una cadena del tono de gris especificado
     * @param g tono de gris (0-255)
     * @return la cadena con el color
     */
    static String rgb(int g) {
        return "rgb("+ g +","+ g +","+ g +");";
    }
    
    /**
     * Proporciona un color de una rampa de colores predeterminada, en fomato 
     * hexadecimal
     * @param layer entrada en la rampa de color
     * @return color en formato hexadecimal
     */
    static String rampColor(int layer) {
        String colors[] = { 
            "FA0000", 
            "FA2A00", 
            "FB5500", 
            "FC7F00", 
            "FDAA00", 
            "FED400", 
            "FFFF00",
            "D4FF00", 
            "AAFF00", 
            "7FFF00", 
            "55FF00", 
            "2AFF00", 
            "00FF00"
        };
        
        return "#"+ colors[layer % colors.length] + ";";
    }

    
    ////////////////////////////////////////////////////////////////////////////
    // GENERADORES DE GRAFOS ALEATORIOS
    
    /**
     * Genera un grafo aleatorio con el modelo G(n,m) de Erdös y Rényi. En el cual se crean n vértices 
     * y elegir uniformemente al azar m distintos pares de distintos vértices para crear m aristas .
     * @param n Número de nodos
     * @param m Número de aristas
     * @param directed Generar el grafo dirigido o no-dirigido
     * @param self Permitir aristas entre un mismo nodo
     * @return El grafo aletorio generado con el modelo de Erdos
     */
    public static Graph randomErdos(int n, int m, boolean directed, boolean self, int s1, int s2) {
        Graph g = new SingleGraph("Erdos");
        g.display();
        randomErdos(g, n, m, directed, self, s1, s2);
        return g;
    }
    public static Graph randomErdos(Graph g, int n, int m, boolean directed, boolean self, int s1, int s2) {
        // Generar n nodos
        for(int i=0; i<n; i++) {
            DAA.pause(s1);
            Node node = g.addNode(NODE_NAME_PREFIX+i);
            format(node);
        }
        
        // Generar m aristas entre nodos aleatorios
        for(int i=0; i<m; i++) {
            try {
                int u = randomInt(0, n);
                int v = randomInt(0, n);
                if(u!=v) {
                    addEdge(g, NODE_NAME_PREFIX+u, NODE_NAME_PREFIX+v, s2);
                } else if( self ) {
                    addEdge(g, NODE_NAME_PREFIX+u, NODE_NAME_PREFIX+v, s2);
                } else {
                    i--;
                }
            } catch( Exception e) {
                //i--;
            }
        }
        
        System.out.println("Ok");
        return g;
    }
    
    /**
     * Genera un grafo aleatorio con el el modelo G(n,p) de Gilbert. En el cual se crean n vértices 
     * y poner una arista entre cada par independiente y uniformemente con probabilidad p.
     * @param n Número de nodos
     * @param p Probabilidad de generar una arista
     * @param directed Generar el grafo dirigido o no-dirigido
     * @param self Permitir aristas entre un mismo nodo
     * @return El grafo aletorio generado con el modelo de Gilbert
     */
    public static Graph randomGilbert(int n, double p, boolean directed, boolean self, int s1, int s2) {
        Graph g = new SingleGraph("Gilbert");
        g.display();
        return randomGilbert(g, n, p, directed, self, s1, s2);
    }
    public static Graph randomGilbert(Graph g, int n, double p, boolean directed, boolean self, int s1, int s2) {    
        g.setStrict(false);
        // Generar n nodos
        for(int i=0; i<n; i++) {
            DAA.pause(s1);
            Node node = g.addNode(NODE_NAME_PREFIX+i);
            format(node);
        }
        
        // Generar con probabilidad p una arista entre cada par de nodos
        for(int i=0; i<n; i++) {
            for(int j=0; j<n; j++) {
                if( Math.random() < p ) {
                    try {
                        if(i!=j) {
                            addEdge(g, NODE_NAME_PREFIX+i, NODE_NAME_PREFIX+j, s2);
                        } else if( self ) {
                            addEdge(g, NODE_NAME_PREFIX+i, NODE_NAME_PREFIX+j, s2);
                        }
                    } catch(Exception e) {}
                }
            }
        }
        
        return g;
    }
    
    /**
     * Genera un grafo aleatorio con el modelo Barabási-Albert. En el cual se colocan n vértices 
     * uno por uno, asignando a cada uno d aristas a vértices distintos de tal manera que 
     * la probabilidad de que el vértice nuevo se conecte a un vértice existente v 
     * es proporcional a la cantidad de aristas que v tiene actualmente
     * @param n Número de nodos
     * @param d Número de aristas por nodo
     * @param directed Generar el grafo dirigido o no-dirigido
     * @param self Permitir aristas entre un mismo nodo
     * @return El grafo aletorio generado con el modelo de Barabási-Albert
     */
    public static Graph randomBarabasi(int n, double d, boolean directed, boolean self, int sleep) {
        Graph g = new SingleGraph("Barabasi");
        g.display();
        return randomBarabasi(g, n, d, directed, self, sleep);
    }
    public static Graph randomBarabasi(Graph g, int n, double d, boolean directed, boolean self, int sleep) {
        // Para cada nodo nuevo (1,2,...,n)
        for(int u=1; u<n; u++) {
            try {
                format(g.addNode(NODE_NAME_PREFIX + u));

                // Verificar con todos los nodos anteriores (0,1,...,u)
                int []randomNodes = randomArray(u);
                for(int v=0; v<u; v++) {
                    double p = 1- (g.getNode(NODE_NAME_PREFIX+randomNodes[v]).getDegree() / d);
                    if( Math.random() < p ) {
                        try {
                            if( randomNodes[v] != u) {
                                addEdge(g, NODE_NAME_PREFIX+randomNodes[v], NODE_NAME_PREFIX+u, sleep);
                            } else if( self ) {
                                addEdge(g, NODE_NAME_PREFIX+randomNodes[v], NODE_NAME_PREFIX+u, sleep);
                            }
                        } catch(Exception e) { System.out.println(e);}
                    } 
                } 
            } catch(Exception e) {}
        }
        
        return g;
    }
        
    /**
     * Genera un grafo aleatorio con el modelo Geográfico.En el cual se colocan 
     * n vértices en un rectángulo unitario con coordenadas uniformes (o 
     * normales) y se coloca una arista entre cada par que queda en distancia r 
     * o menor 
     * @param n Número de nodos
     * @param r Distancia máxima para crear una arista
     * @param directed Generar el grafo dirigido o no-dirigido
     * @param self Permitir aristas entre un mismo nodo
     * @param s1 Tiempo de espera para agregar un nodo
     * @param s2 Tiempo de espera para agregar una arista
     * @return El grafo aletorio generado con el modelo Geográfico simple
     */
    public static Graph randomGeo(int n, double r, boolean directed, boolean self, int s1, int s2) {
        Graph g = new SingleGraph("Geographic");
        g.display();
        return randomGeo(g, n, r, directed, self, s1, s2);
    }
    public static Graph randomGeo(Graph g, int n, double r, boolean directed, boolean self, int s1, int s2) {
        // Generar n nodos con coordenadas en el espacio ((0,0),(1,1))
        for(int i=0; i<n; i++) {
             DAA.pause(s1);
            Node v = g.addNode(NODE_NAME_PREFIX + i);
            v.setAttribute("x", Math.random());
            v.setAttribute("y", Math.random());
            format(v);
        }
        
        // Crear una arista entre cada par de nodos que están a distancia <= r
        for(int u=0; u<n; u++) {
            for(int v=0; v<n; v++) {
                double dist = dist(g.getNode(NODE_NAME_PREFIX+u), g.getNode(NODE_NAME_PREFIX+v));
                try {
                    if( u!=v && dist <= r) {
                        addEdge(g, NODE_NAME_PREFIX+u, NODE_NAME_PREFIX+v, s2);
                    }
                    if( u==v && self ) {
                        addEdge(g, NODE_NAME_PREFIX+u, NODE_NAME_PREFIX+v, s2);
                    }
                } catch( Exception e) {}
            }
        }
        
        return g;
    }
    
    /**
     * Genera un grafo de malla
     * @param graph grafo a generar
     * @param n número de nodos, el número total de nodos a crear es n^2
     * @param cross especifica si se generará una malla con triángulos
     * @param tore especifica si se formará una toroide
     * @param sleep tiempo de espera para la animación
     */
    public static Graph randomGrid(int n, boolean cross, boolean tore, int sleep) {
        Graph graph = new SingleGraph("grid");
        graph.display(false);
        return randomGrid(graph, n, cross, tore, sleep);
    }
    public static Graph randomGrid(Graph graph, int n, boolean cross, boolean tore, int sleep) {   
        Generator gen = new GridGenerator(cross, tore);
        
        graph.setAttribute(DAA.GENERATOR_ATTR, DAA.GENERATOR_GRID);

        gen.addSink(graph);
        gen.begin();

        for(int i=0; i<n; i++) {
            gen.nextEvents();
            try { Thread.sleep(sleep); } catch(Exception ex) {}
        }

        gen.end();
        
        graph.nodes().forEach( v -> format(v));
        graph.edges().forEach( e -> e.setAttribute("ui.style", "fill-color:gray;"));
        
        return graph;
    }
    
    /**
     * Genera un grafo con el algoritmo de Dorogovtsev-Mendes (2002)
     * @param n número de nodos
     * @param sleep tiempo de espera para la animación
     * @return el grafo generado
     */
    public static Graph randomDorogovtsev(int n, int sleep) {
        Graph graph = new SingleGraph("Dorogovtsev");
        graph.display();
        
        randomDorogovtsev(graph, n, sleep);
        
        return graph;
    }
    public static Graph randomDorogovtsev(Graph graph, int n, int sleep) {        
        Generator gen = new DorogovtsevMendesGenerator();
        gen.addSink(graph);
        gen.begin();
        
        graph.setAttribute(DAA.GENERATOR_ATTR, DAA.GENERATOR_DOROGOVTSEV);

        for(int i=0; i<n; i++) {
            gen.nextEvents();
            try { Thread.sleep(sleep); } catch(Exception ex) {}
        }

        gen.end();
        
        graph.nodes().forEach( v -> format(v));
        graph.edges().forEach( e -> e.setAttribute("ui.style", "fill-color:gray;"));
        
        return graph;
    }
    
    public static Graph randomGS(Graph graph, int n, int sleep) {
        Generator gen = new BananaTreeGenerator();
        gen.addSink(graph);
        gen.begin();
        
        graph.setAttribute(DAA.GENERATOR_ATTR, DAA.GENERATOR_DOROGOVTSEV);

        for(int i=0; i<n; i++) {
            gen.nextEvents();
            try { Thread.sleep(sleep); } catch(Exception ex) {}
        }

        gen.end();
        
        graph.nodes().forEach( v -> format(v));
        graph.edges().forEach( e -> e.setAttribute("ui.style", "fill-color:gray;"));
        
        return graph;
    }
    
    /**
     * Genera un grafo con el algoritmo de Barabasi-Albert implementado por 
     * GraphStream
     * @param n número de nodos
     * @param mlps número de aristas por nodo
     * @param sleep tiempo de espera para la animación
     * @return el grafo generado
     */
    public static Graph randomBarabasiGS(int n, int mlps, int sleep) {
        Graph graph = new SingleGraph("Dorogovtsev");
        graph.display();
        return randomBarabasiGS(graph, n, mlps, sleep);
    }
    public static Graph randomBarabasiGS(Graph graph, int n, int mlps, int sleep) {
        Generator gen = new BarabasiAlbertGenerator(mlps);
        
        gen.addSink(graph);
        gen.begin();

        for(int i=0; i<n; i++) {
            gen.nextEvents();
            try { Thread.sleep(sleep); } catch(Exception ex) {}
        }

        gen.end();
        
        return graph;
    }
    
    /**
     * Genera un grafo de mundo pequeño implementado por GraphStream
     * @param n número de nodos
     * @param k
     * @param beta
     * @param sleep tiempo de espera para la animación
     * @return el grafo generado
     */
    public static Graph randomSmallWorld(int n, int k, double beta, int sleep) {
        Graph graph = new SingleGraph("SmallWorld");
        graph.display(false);
        return randomSmallWorld(graph, n, k, beta, sleep);
    }
    public static Graph randomSmallWorld(Graph graph, int n, int k, double beta, int sleep) {
        Generator gen = new WattsStrogatzGenerator(n, k, beta);
        
        gen.addSink(graph);
        gen.begin();

        while( gen.nextEvents() ) {
            try { Thread.sleep(sleep); } catch(Exception ex) {}
        }

        gen.end();
        
        return graph;
    }
    
    /**
     * Genera un grafo con el método euclideano, igual que el geográfico
     * @param n número de nodos
     * @param sleep tiempo de espera para la animación
     * @return el grafo generado
     */
    public static Graph randomEuclidean(int n, int sleep) {
        Graph graph = new SingleGraph("Euclidean");
        graph.display();
        return randomEuclidean(graph, n, sleep);
    }
    public static Graph randomEuclidean(Graph graph, int n, int sleep) {
        Generator gen = new RandomEuclideanGenerator();
        
        gen.addSink(graph);
        gen.begin();

        for(int i=0; i<n; i++) {
            gen.nextEvents();
            try { Thread.sleep(sleep); } catch(Exception ex) {}
        }

        gen.end();
        
        return graph;
    }
    
    /**
     * Genera un grafo aleatorio con el metodo indicado por gen
     * @param gen indica el generador que se usará
     * @param size número de nodos que tendrá el grafo
     * @param sleep tiempo de espera para la animación
     * @return 
     */
    public static Graph randomGraph(int gen, int size, int sleep) {
        Graph g = new SingleGraph("random");
        g.display( gen != DAA.GENERATOR_GRID && gen != DAA.GENERATOR_SMALL_WORLD );
        randomGraph(g, gen, size, sleep);
        return g;
    }
    public static Graph randomGraph(Graph g, int gen, int size, int sleep) {
        if( g == null ) 
            g = new SingleGraph("random");
        
        switch(gen) {
            case DAA.GENERATOR_BARABASI:    randomBarabasi(g, size, 4, DIRECTED_GRAPH, CYCLES_NOT_ALLOWED, sleep/size); break;
            case DAA.GENERATOR_BARABASI_GS: randomBarabasiGS(g, size, 4, sleep/size); break;
            case DAA.GENERATOR_DOROGOVTSEV: randomDorogovtsev(g, size, sleep/size); break;
            case DAA.GENERATOR_ERDOS:       randomErdos(g, size, size*size/10, DIRECTED_GRAPH, CYCLES_NOT_ALLOWED, (sleep/2)/size, (sleep/2)/size); break;
            case DAA.GENERATOR_EUCLIDEAN:   randomEuclidean(g, size, sleep/size); break;
            case DAA.GENERATOR_GILBERT:     randomGilbert(g, size, 0.1, DIRECTED_GRAPH, CYCLES_NOT_ALLOWED, (sleep/2)/size, (sleep/2)/size); break;
            case DAA.GENERATOR_GRID:        randomGrid(g, (int) Math.floor( Math.sqrt(size) ), false, false, sleep/(int) Math.floor( Math.sqrt(size) )); break;
            case DAA.GENERATOR_SMALL_WORLD: randomSmallWorld(g, size, 2, 0.7, sleep/size); break;
            case DAA.GENERATOR_GEO:
            default:                        randomGeo(g, size, 0.12 + 0.0002 * (1000 - size), DIRECTED_GRAPH, CYCLES_NOT_ALLOWED, (sleep/2)/size, (sleep/2)/size);
        }
        
        g.setAttribute(DAA.GENERATOR_ATTR, gen);
        
        return g;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // ALGORITMOS
    
    /**
     * Calcula el árbol de búsqueda a lo ancho (BFS) para el grafo, a partir de 
     * un nodo.
     * @param s El nodo semilla para calcular el árbol.
     * @return  El árbol de búsqueda a lo ancho.
     */
    public static Graph BFS(Graph g, int s, int sleep) {
        Graph bfs_tree = new SingleGraph("BFS");
        bfs_tree.display();
        BFS(bfs_tree, g, s, sleep);
        return bfs_tree;
    }
    public static void BFS(Graph bfs_tree, Graph g, int s, int sleep) {
        ArrayList<HashSet<Node>> L = new ArrayList<>();
        Node seed = g.getNode(s);
        HashSet<Node> added = new HashSet<>();
        
        bfs_tree.setStrict(false);
        bfs_tree.addNode(seed.getId()).setAttribute("ui.style", "fill-color:"+ rampColor(0));
        
        seed.setAttribute("ui.style", "stroke-width:20;");
        seed.setAttribute("ui.style", "size:20;");
        seed.setAttribute("ui.style", "fill-color:"+ rampColor(0));
        
        L.add(0, new HashSet<>());
        L.get(0).add(seed);
        added.add(seed);
                
        g.edges().forEach( e -> e.setAttribute("bfs", false) );
        g.nodes().forEach( n -> n.setAttribute("ui.style", "z-index:3;") );
        
        for( Node n : g ) {
            n.setAttribute("ui.style", "z-index:3;");
        }
        
        for(int layer = 0; layer < L.size(); layer++) {
            HashSet<Node> nextLayer = new HashSet<>();
            HashSet<Node> curLayer = L.get(layer);
            final int ly = layer;
            for( Node n:curLayer ) { 
                String fillColor = "fill-color:"+ rampColor(layer+1);
                n.edges().forEach(e -> {
                    Node m = e.getNode0() == n ? e.getNode1() : e.getNode0();
                    if( !nextLayer.contains(m) && !added.contains(m) ) {
                        Node nn = bfs_tree.addNode(n.getId());
                        Node nm = bfs_tree.addNode(m.getId());
                        
                        if( ly == 0 ) {
                            nn.setAttribute("ui.style", "stroke-width:20;");
                            nn.setAttribute("ui.style", "size:20;");
                            nn.setAttribute("ui.style", "fill-color:"+ rampColor(0));
                        }
                        
                        bfs_tree.addEdge(n.getId() +"->"+ m.getId(), nn, nm);
                        nm.setAttribute("ui.style", fillColor);

//                        m.setAttribute("ui.style", "stroke-color:black;");
//                        m.setAttribute("ui.style", "stroke-width:1;");
                        m.setAttribute("ui.style", fillColor);
                        

                        e.setAttribute("ui.style", "fill-color:black;");
//                        e.setAttribute("ui.style", "stroke-width:10;");
                        e.setAttribute("ui.style", "size:2;");
                        e.setAttribute("ui.style", "z-index:2;");
                        e.setAttribute("bfs", true);
                        
                        nextLayer.add(m);
                        added.add(m);
                        try { Thread.sleep(sleep); } catch(Exception ex) {}
                    } 
                });
            }
            if(!nextLayer.isEmpty()) {
                L.add(nextLayer);
            }
        }
    }
    
    /**
     * Parte recursiva del algoritmo de búsqueda en profundidad (DFS).
     * @param seed Nodo semilla para calcular el subárbol de busqueda en profundidad.
     * @param dfs Grafo en el que se está construyendo el árbol de búsqueda en profundidad.
     * @param added Conjunto de nodos que ya han sido añadidos al árbol de búsqueda en profundidad.
     */
    public static void DFS_R(Node seed, Graph dfs, HashSet<Node> added, int sleep, int layer) {
        added.add(seed);

        seed.edges().forEach(e -> {
            Node m = e.getNode0() == seed ? e.getNode1() : e.getNode0();
            if(!added.contains(m)) {
                Node nn = dfs.addNode(seed.getId());
                Node nm = dfs.addNode(m.getId());
                
                if( layer == 0 ) {
                    nn.setAttribute("ui.style", "stroke-width:20;");
                    nn.setAttribute("ui.style", "size:20;");
                    nn.setAttribute("ui.style", "fill-color:"+ rampColor(layer));
                }
                
                nm.setAttribute("ui.style", "fill-color:"+ rampColor(layer+1));
                
                dfs.addEdge(seed.getId() +"->"+ m.getId(),nn, nm);
                m.setAttribute("ui.style", "fill-color:"+ rampColor(layer+1));
                
                e.setAttribute("ui.style", "fill-color:red;");
                e.setAttribute("ui.style", "size:2;");
                e.setAttribute("ui.style", "z-index:2;");
                e.setAttribute("dfs", true);
                
                try { Thread.sleep(sleep/2); } catch(Exception ex) {}
                
                DFS_R(m, dfs, added, sleep, layer + 1);
                
                e.setAttribute("ui.style", "fill-color:black;");
                e.setAttribute("ui.style", "size:2;");
                e.setAttribute("ui.style", "z-index:2;");
                e.setAttribute("dfs", true);
                
                try { Thread.sleep(sleep/2); } catch(Exception ex) {}
            }
        } );
    }
    
    /**
     * Parte no recursiva del algoritmo de búsqueda en profundidad (DFS).
     * @param s El nodo semilla para calcular el árbol.
     * @return El árbol de búsqueda en profundidad.
     */
    public static Graph DFS(Graph g, int s, int sleep) {
        Graph dfs_tree = new SingleGraph("DFS");
        dfs_tree.display();
        DFS(dfs_tree, g, s, sleep);
        return dfs_tree;
    }
    public static void DFS(Graph dfs_tree, Graph g, int s, int sleep) {
        Node seed = g.getNode(s);
        HashSet<Node> added = new HashSet<>();
                
        seed.setAttribute("ui.style", "fill-color:"+ rampColor(0));
        seed.setAttribute("ui.style", "size:20;");
        seed.setAttribute("ui.style", "stroke-width:20;");
        
        dfs_tree.setStrict(false);
        g.edges().forEach(e -> { e.setAttribute("dfs", false); });
        g.nodes().forEach(n -> { n.setAttribute("ui.style", "z-index:3;"); });
        
        DFS_R(seed, dfs_tree, added, sleep, 0);
        
        try { Thread.sleep(sleep); } catch(Exception ex) {}
    }
    
    /**
     * Algoritmo de Dijkstra - determina el siguiente nodo a añadir al árbol de caminos mínimos
     * @param not_visited Conjunto de nodos que no han sido añadidos al árbol
     * @param seed El nodo fuente para el árbol de Dijkstra
     * @return El siguiente nodo a añadir
     */
    protected static Node nextNode(HashSet<Node> not_visited, int seed) {
        Node retVal = null;
        
        for( Node n : not_visited ) {
            if( retVal == null || (Double)n.getAttribute(DIJKSTRA_LENGTH_FROM_SEED + seed) < (Double)retVal.getAttribute(DIJKSTRA_LENGTH_FROM_SEED + seed) ) {
                retVal = n;
            }
        }
        
        not_visited.remove(retVal);
        return retVal;
    }
    
    /**
     * Algoritmo de Dijkstra - actualiza los valores de distancia para los nodos que están al alcance del siguiente nodo a añadir
     * @param n El siguiente nodo a añadir
     * @param seed El nodo fuente para el árbol de Dijkstra
     */
    protected static void updateDijkstraData(Node n, int seed) {
        n.edges().forEach(e -> {
            Node vecino = e.getNode0() == n ? e.getNode1() : e.getNode0();
//            if( e.getNode0() == n ) {
                double len = (Double)n.getAttribute(DIJKSTRA_LENGTH_FROM_SEED + seed) + (Double)e.getAttribute(WEIGHT_PARAM);
                if( len < (Double)vecino.getAttribute(DIJKSTRA_LENGTH_FROM_SEED + seed)) {
                    vecino.setAttribute(DIJKSTRA_LENGTH_FROM_SEED + seed, len);
                    vecino.setAttribute(DIJKSTRA_PREV_NODE_FOR_SEED + seed, n);
                    vecino.setAttribute(DIJKSTRA_PREV_EDGE_FOR_SEED + seed, e);
                    
//                    e.setAttribute("ui.style", "fill-color:black;");
//                    e.setAttribute("ui.style", "size:2;");
//                    e.setAttribute("ui.style", "z-index:2;");
//                    e.setAttribute("dijkstra", true);
                }
//            }
        });
    }
    
    
    
    public static Graph Dijkstra(Graph g, int seed, int sleep) {
        Graph dijkstra_tree = new SingleGraph("dijkstra");
        
        dijkstra_tree.display();
        Dijkstra(dijkstra_tree, g, seed, sleep);
        
        return dijkstra_tree;
    }
    
    /**
     * Algoritmo de Dijkstra - calcula el árbol de caminos mínimos a partir de un nodo fuente
     * @param seed El nodo fuente
     * @return  El árbol de caminos mínimos
     */
    public static Graph Dijkstra(Graph retVal, Graph g, int seed, int sleep) {
        HashSet<Node> nodos = new HashSet<>();
        
        g.nodes().forEach(n -> {
            nodos.add(n);
            n.setAttribute(DIJKSTRA_LENGTH_FROM_SEED + seed, Double.POSITIVE_INFINITY);
            n.setAttribute(DIJKSTRA_PREV_NODE_FOR_SEED + seed, n);
        });
        
        Node s = g.getNode(seed);
        s.setAttribute(DIJKSTRA_LENGTH_FROM_SEED + seed, 0.0);
        
        retVal.setStrict(false);
        retVal.setAttribute("ui.antialias", true);
        
        Node r = retVal.addNode(s.getId());
        r.setAttribute("ui.style", "fill-color:"+ rampColor(0));
        r.setAttribute("ui.style", "size:20;");
        r.setAttribute("ui.style", "stroke-width:20;");
        r.setAttribute("ui.style", "z-index:3;");
        
        nodos.remove(s);
        s.setAttribute("ui.style", "fill-color:"+ rampColor(0));
        s.setAttribute("ui.style", "size:20;");
        s.setAttribute("ui.style", "stroke-width:20;");
        s.setAttribute("ui.style", "z-index:3;");
                
        Node n = s;
        while( !nodos.isEmpty() ) {
            updateDijkstraData(n, seed);
            n = nextNode(nodos, seed);
            
            String color = randomColor(128, 220);
            String shape = randomShape();
            
            n.setAttribute("ui.style", "fill-color:"+ color);
            n.setAttribute("ui.style", "z-index:3;");
            n.setAttribute("ui.style", "shape:" + shape);
            n.setAttribute("ui.style", "size:15;");
            
            Node nn = retVal.addNode(n.getId());
            nn.setAttribute("ui.label", String.format("%d", Math.round((double)n.getAttribute(DIJKSTRA_LENGTH_FROM_SEED + seed))));
            nn.setAttribute("ui.style", "fill-color:"+ color);
            nn.setAttribute("ui.style", "z-index:3;");
            nn.setAttribute("ui.style", "shape:" + shape);
            nn.setAttribute("ui.style", "size:20;");
            
            Node prev = (Node)n.getAttribute(DIJKSTRA_PREV_NODE_FOR_SEED + seed);
            Edge e = retVal.addEdge(prev.getId() + "->" + n.getId(), prev.getId(), n.getId());
            
            e.setAttribute("ui.style", "fill-color:black;");
            e.setAttribute("ui.style", "size:1;");
            e.setAttribute("ui.style", "z-index:2;");
            e.setAttribute("dijkstra", true);
                        
            e = (Edge)n.getAttribute(DIJKSTRA_PREV_EDGE_FOR_SEED + seed);
            e.setAttribute("ui.label", String.format("%d", Math.round((double)e.getAttribute(WEIGHT_PARAM))));
            e.setAttribute("ui.style", "fill-color:black;");
            e.setAttribute("ui.style", "size:1;");
            e.setAttribute("ui.style", "z-index:2;");
            e.setAttribute("dijkstra", true);
            
            pause(sleep);
        }
                
        return retVal;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // METODO PRINCIPAL
    
    public static void main(String args[]) {
        System.setProperty("org.graphstream.ui", "swing");

        int size = 200, sleep = 1000;
        Graph g = DAA.randomGraph(DAA.GENERATOR_DOROGOVTSEV, size, sleep);
        
        g.setAttribute("ui.antialias", true);
        g.nodes().forEach( n -> format(n));
        g.edges().forEach( e -> e.setAttribute("ui.style", "fill-color:gray;"));
        
        genRandomEdgeWeights(g, 50, 100);

//        Graph clon = Graphs.clone(g);
//        clon.edges().forEach( e ->  e.setAttribute("ui.style", "fill-color:gray;") );
//        clon.display(true);
//
//        try { Thread.sleep(pausa); } catch(Exception ex) {}
//        Graph bfs = BFS(g, 1, speed/size);
//
//        try { Thread.sleep(pausa); } catch(Exception ex) {}
//        Graph dfs = DFS(clon, 1, sleep/size);

       Graph dijkstra = Dijkstra(g, 1, 20 * sleep/size);
    }
}

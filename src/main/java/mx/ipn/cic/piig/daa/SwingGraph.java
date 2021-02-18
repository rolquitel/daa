/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ipn.cic.piig.daa;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.layout.Layouts;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.view.*;

public class SwingGraph  {
    public Graph g1, g2;
    public Graph bfs, dfs, dijkstra;
    JPanel panel;
    int n = 200, speed = 30000, max = 1000, gen = DAA.GENERATOR_GRID;

    public static void main(String args[]) {
        System.setProperty("org.graphstream.ui", "swing");
        SwingGraph jf = new SwingGraph();
        jf.display();
        jf.algos();
    }
    
    void assignGraphToPanel(Graph g, JPanel p, boolean autoLayout ) {
        Viewer viewer = new SwingViewer(g, SwingViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        
        if( autoLayout )
            viewer.enableAutoLayout();
        
        View viewPanel = viewer.addDefaultView(false);
        
        p.add((JPanel)viewPanel);
    }
    
    void algos() {
        try {
//            Thread bfsThread = new Thread( new Runnable() {
//                @Override
//                public void run() {
//                    System.out.println("BFS Thread start()");
//                    DAA.BFS(bfs, g1, 1, speed / n);
//                    System.out.println("BFS Thread stop()");
//                }
//            });
//            Thread dfsThread = new Thread( new Runnable() {
//                @Override
//                public void run() {
//                    System.out.println("DFS Thread start()");
//                    DAA.DFS(dfs, g2, 1, speed / n);
//                    System.out.println("DFS Thread stop()");
//                }
//            });
            Thread dijkstraThread = new Thread( new Runnable() {
                @Override
                public void run() {
                    System.out.println("Dijkstra Thread start()");
                    DAA.Dijkstra(dijkstra, g1, 1, speed / n);
                    System.out.println("Dijkstra Thread stop()");
                }
            });
            
//            bfsThread.start();
//            dfsThread.start();
            dijkstraThread.start();
        } catch(Exception e) {}
    }

    private void display() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        panel = new JPanel(new GridLayout(1,2)){
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(1024, 720);
            }
        };
                
        g1 = new SingleGraph("g1");
        DAA.randomGraph(g1, gen, n, 0);
        g1.nodes().forEach( n -> DAA.format(n));
        g1.edges().forEach( e -> e.setAttribute("ui.style", "fill-color:gray;"));
        DAA.genRandomEdgeWeights(g1, 5, 15);
        
//        g2 = Graphs.clone(g1);
//        g2.edges().forEach( e ->  e.setAttribute("ui.style", "fill-color:gray;") );
//        
//        bfs = new SingleGraph("bfs");
//        dfs = new SingleGraph("dfs");
        dijkstra = new SingleGraph("dijkstra");
        
        g1.setAttribute("ui.antialias", true);
//        g2.setAttribute("ui.antialias", true);
//        bfs.setAttribute("ui.antialias", true);
//        dfs.setAttribute("ui.antialias", true);
        dijkstra.setAttribute("ui.antialias", true);
        
        assignGraphToPanel(g1, panel, gen != DAA.GENERATOR_GRID && gen != DAA.GENERATOR_SMALL_WORLD);
//        assignGraphToPanel(g2, panel);
//        assignGraphToPanel(bfs, panel);
//        assignGraphToPanel(dfs, panel);
        assignGraphToPanel(dijkstra, panel, true);
       
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

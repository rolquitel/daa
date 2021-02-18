/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ipn.cic.piig.daa.voraces;

import java.util.Vector;


/**
 *
 * @author rolando
 */

class Tarea implements Comparable {
    public int inicio, finalizacion;

    @Override
    public int compareTo(Object o) {
        return this.finalizacion - ((Tarea)o).finalizacion;
    }
}

public class PlanificacionDeIntervalos {
    public static void main(String args[]) {
        Vector<Tarea> tareas = new Vector();
    }
    
}

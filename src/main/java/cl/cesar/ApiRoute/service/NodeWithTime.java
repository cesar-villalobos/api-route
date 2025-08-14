package cl.cesar.ApiRoute.service;

import cl.cesar.ApiRoute.model.Location;

/**
 * Clase auxiliar para el algoritmo de Dijkstra.
 * Representa un nodo con el tiempo acumulado para llegar a él,
 * facilitando la ordenación en la cola de prioridad.
 */
public class NodeWithTime {

    private Location location;
    private int time;

    public NodeWithTime(Location location, int time) {
        this.location = location;
        this.time = time;
    }

    public Location getLocation() {
        return location;
    }

    public int getTime() {
        return time;
    }
}
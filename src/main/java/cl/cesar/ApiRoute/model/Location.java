package cl.cesar.ApiRoute.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa una ubicación en el grafo.
 * Contiene el nombre de la ubicación y una lista de sus conexiones salientes.
 */
public class Location {

    private String name;
    private List<Connection> connections;

    public Location(String name) {
        this.name = name;
        this.connections = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    /**
     * Añade una nueva conexión saliente desde esta ubicación.
     * @param destination La ubicación de destino de la conexión.
     * @param time El tiempo de viaje (peso) de la conexión.
     */
    public void addConnection(Location destination, int time) {
        this.connections.add(new Connection(destination, time));
    }

    @Override
    public String toString() {
        return "Location{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(name, location.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
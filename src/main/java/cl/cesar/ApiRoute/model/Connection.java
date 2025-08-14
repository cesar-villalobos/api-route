package cl.cesar.ApiRoute.model;

/**
 * Representa una conexión unidireccional (arista) entre dos ubicaciones.
 * Contiene la ubicación de destino y el tiempo de viaje.
 */
public class Connection {

    private Location destination;
    private int time;

    public Connection(Location destination, int time) {
        this.destination = destination;
        this.time = time;
    }

    public Location getDestination() {
        return destination;
    }

    public int getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "destination=" + destination.getName() +
                ", time=" + time +
                '}';
    }
}
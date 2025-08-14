package cl.cesar.ApiRoute.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Representa el grafo de ubicaciones y conexiones.
 * Utiliza un mapa para almacenar las ubicaciones de forma eficiente.
 */
public class Graph {

    private Map<String, Location> locations;

    public Graph() {
        this.locations = new HashMap<>();
    }

    /**
     * Añade una nueva ubicación al grafo.
     * @param name El nombre de la ubicación.
     * @return La instancia de la ubicación añadida o la existente si ya estaba en el grafo.
     */
    public Location addLocation(String name) {
        return locations.computeIfAbsent(name, Location::new);
    }

    /**
     * Añade una conexión unidireccional entre dos ubicaciones.
     * Si las ubicaciones no existen, las crea automáticamente.
     * @param locStart Nombre de la ubicación de origen.
     * @param locEnd Nombre de la ubicación de destino.
     * @param time Tiempo de viaje entre las ubicaciones.
     */
    public void addConnection(String locStart, String locEnd, int time) {
        Location origin = addLocation(locStart);
        Location destination = addLocation(locEnd);
        origin.addConnection(destination, time);
    }

    /**
     * Obtiene una ubicación por su nombre.
     * @param name El nombre de la ubicación a buscar.
     * @return La instancia de la ubicación o null si no existe.
     */
    public Location getLocation(String name) {
        return locations.get(name);
    }

    /**
     * Obtiene todos los nombres de las ubicaciones del grafo.
     * @return Un conjunto de los nombres de todas las ubicaciones.
     */
    public Set<String> getLocationNames() {
        return locations.keySet();
    }

    /**
     * Limpia todas las ubicaciones y conexiones del grafo.
     */
    public void clearGraph() {
        this.locations.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        locations.values().forEach(location -> {
            sb.append("Ubicación: ").append(location.getName()).append("\n");
            location.getConnections().forEach(connection ->
                sb.append("  -> Destino: ").append(connection.getDestination().getName())
                  .append(", Tiempo: ").append(connection.getTime()).append(" min\n")
            );
        });
        return sb.toString();
    }
}
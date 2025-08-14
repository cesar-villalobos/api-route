package cl.cesar.ApiRoute.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cl.cesar.ApiRoute.api.RouteResponse;
import cl.cesar.ApiRoute.model.Connection;
import cl.cesar.ApiRoute.model.Graph;
import cl.cesar.ApiRoute.model.Location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class RouteService {

    private Graph graph = new Graph();

    /**
     * Carga los datos de las conexiones desde un archivo CSV y construye el grafo.
     * El archivo debe tener el formato: loc_start;loc_end;time
     *
     * @param file El archivo CSV a procesar.
     * @throws IOException Si ocurre un error al leer el archivo.
     */
    public void loadData(MultipartFile file) throws IOException {
        graph.clearGraph(); // Limpiamos el grafo actual para evitar duplicados
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 3) {
                    try {
                        String locStart = parts[0].trim();
                        String locEnd = parts[1].trim();
                        int time = Integer.parseInt(parts[2].trim());
                        graph.addConnection(locStart, locEnd, time);
                    } catch (NumberFormatException e) {
                        // Opcional: Loggear la línea que no se pudo parsear
                        System.err.println("Advertencia: Línea de CSV con formato inválido -> " + line);
                    }
                }
            }
        }
    }

    /**
     * Encuentra la ruta más rápida (con el menor tiempo de viaje) entre dos ubicaciones
     * utilizando el algoritmo de Dijkstra.
     *
     * @param origin Nombre de la ubicación de origen.
     * @param destination Nombre de la ubicación de destino.
     * @return Un objeto RouteResponse con la ruta y el tiempo total. Si no hay ruta,
     * el tiempo total será -1.
     */
    public RouteResponse findFastestRoute(String origin, String destination) {
        Location originLocation = graph.getLocation(origin);
        Location destinationLocation = graph.getLocation(destination);

        if (originLocation == null || destinationLocation == null) {
            return new RouteResponse(Collections.emptyList(), -1);
        }

        // Estructuras de datos para el algoritmo de Dijkstra
        Map<Location, Integer> times = new HashMap<>(); // Tiempos más cortos desde el origen
        Map<Location, Location> predecessors = new HashMap<>(); // Para reconstruir la ruta
        PriorityQueue<NodeWithTime> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(NodeWithTime::getTime));

        // 1. Inicialización
        graph.getLocationNames().forEach(name -> times.put(graph.getLocation(name), Integer.MAX_VALUE));
        times.put(originLocation, 0);
        priorityQueue.add(new NodeWithTime(originLocation, 0));

        // 2. Bucle principal de Dijkstra
        while (!priorityQueue.isEmpty()) {
            Location currentLocation = priorityQueue.poll().getLocation();

            if (currentLocation.equals(destinationLocation)) {
                break; // Se encontró la ruta más corta al destino
            }

            for (Connection connection : currentLocation.getConnections()) {
                Location neighbor = connection.getDestination();
                int newTime = times.get(currentLocation) + connection.getTime();

                if (newTime < times.get(neighbor)) {
                    times.put(neighbor, newTime);
                    predecessors.put(neighbor, currentLocation);
                    priorityQueue.add(new NodeWithTime(neighbor, newTime));
                }
            }
        }

        // 3. Reconstrucción de la ruta y preparación de la respuesta
        List<String> fastestRoute = new ArrayList<>();
        int totalTime = times.getOrDefault(destinationLocation, -1);

        if (totalTime != -1) {
            Location step = destinationLocation;
            while (step != null) {
                fastestRoute.add(step.getName());
                step = predecessors.get(step);
            }
            Collections.reverse(fastestRoute);
        }

        return new RouteResponse(fastestRoute, totalTime);
    }
}
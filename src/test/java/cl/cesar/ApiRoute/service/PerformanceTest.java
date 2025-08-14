package cl.cesar.ApiRoute.service;

import cl.cesar.ApiRoute.api.RouteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de rendimiento para verificar que la API cumple con los requisitos de tiempo de respuesta
 * y puede manejar archivos grandes.
 */
public class PerformanceTest {

    private RouteService routeService;

    @BeforeEach
    void setUp() {
        routeService = new RouteService();
    }

    @Test
    void testPerformanceWith10000Connections() throws IOException {
        // Generar un archivo CSV con 10,000 conexiones
        StringBuilder csvContent = new StringBuilder();
        
        // Crear una red en forma de malla con múltiples caminos
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                if (i < 99) {
                    csvContent.append("NODE_").append(i).append(";NODE_").append(i + 1).append(";").append(1 + (int)(Math.random() * 100)).append("\n");
                }
                if (j < 99) {
                    csvContent.append("NODE_").append(i).append(";NODE_").append(j + 1).append(";").append(1 + (int)(Math.random() * 100)).append("\n");
                }
            }
        }

        MockMultipartFile file = new MockMultipartFile(
                "data", 
                "large_data.csv", 
                "text/csv", 
                csvContent.toString().getBytes(StandardCharsets.UTF_8)
        );

        // Medir tiempo de carga
        long startTime = System.currentTimeMillis();
        routeService.loadData(file);
        long loadTime = System.currentTimeMillis() - startTime;

        System.out.println("Tiempo de carga de 10,000+ conexiones: " + loadTime + "ms");
        
        // El archivo debe cargarse relativamente rápido
        assertTrue(loadTime < 5000, "La carga debe completarse en menos de 5 segundos");

        // Probar múltiples consultas de ruta y medir tiempo
        for (int i = 0; i < 10; i++) {
            startTime = System.currentTimeMillis();
            RouteResponse response = routeService.findFastestRoute("NODE_0", "NODE_99");
            long routeTime = System.currentTimeMillis() - startTime;

            System.out.println("Consulta " + (i + 1) + " - Tiempo: " + routeTime + "ms, Ruta encontrada: " + (response.getTotalTime() != -1));
            
            // Verificar que cada consulta se complete en menos de 300ms
            assertTrue(routeTime < 300, "Cada consulta debe completarse en menos de 300ms. Tiempo actual: " + routeTime + "ms");
        }
    }

    @Test
    void testPerformanceWithComplexGraph() throws IOException {
        // Crear un grafo más complejo con múltiples caminos alternativos
        StringBuilder csvContent = new StringBuilder();
        
        // Red tipo estrella con múltiples hubs
        for (int hub = 1; hub <= 10; hub++) {
            for (int node = 1; node <= 50; node++) {
                int time = 10 + (int)(Math.random() * 90);
                csvContent.append("HUB_").append(hub).append(";NODE_").append(node).append(";").append(time).append("\n");
                csvContent.append("NODE_").append(node).append(";HUB_").append(hub).append(";").append(time).append("\n");
            }
        }
        
        // Conexiones entre hubs
        for (int hub1 = 1; hub1 <= 10; hub1++) {
            for (int hub2 = hub1 + 1; hub2 <= 10; hub2++) {
                int time = 20 + (int)(Math.random() * 80);
                csvContent.append("HUB_").append(hub1).append(";HUB_").append(hub2).append(";").append(time).append("\n");
                csvContent.append("HUB_").append(hub2).append(";HUB_").append(hub1).append(";").append(time).append("\n");
            }
        }

        MockMultipartFile file = new MockMultipartFile(
                "data", 
                "complex_data.csv", 
                "text/csv", 
                csvContent.toString().getBytes(StandardCharsets.UTF_8)
        );

        routeService.loadData(file);

        // Probar consultas entre nodos distantes
        long startTime = System.currentTimeMillis();
        RouteResponse response = routeService.findFastestRoute("NODE_1", "NODE_50");
        long routeTime = System.currentTimeMillis() - startTime;

        System.out.println("Consulta compleja - Tiempo: " + routeTime + "ms");
        assertTrue(routeTime < 300, "La consulta compleja debe completarse en menos de 300ms");
        assertNotEquals(-1, response.getTotalTime(), "Debe encontrar una ruta válida");
    }
}

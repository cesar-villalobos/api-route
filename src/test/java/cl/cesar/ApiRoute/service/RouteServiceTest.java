package cl.cesar.ApiRoute.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import cl.cesar.ApiRoute.api.RouteResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RouteServiceTest {

    @InjectMocks
    private RouteService routeService;

    // Aquí no necesitamos mocks, ya que el Graph es una dependencia interna del servicio.
    // Solo crearemos la instancia real del servicio.

    @BeforeEach
    void setUp() {
        // Inicializamos una nueva instancia del servicio antes de cada prueba.
        // Esto asegura que cada prueba tenga un grafo "limpio".
        routeService = new RouteService();
    }

    // --- Pruebas para loadData ---

    @Test
    void testLoadDataSuccessfully() throws IOException {
        String csvContent = "loc_start;loc_end;time\nCP1;R11;84\nR11;R12;20\nR12;R13;9";
        MockMultipartFile file = new MockMultipartFile("data", "data.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8));

        routeService.loadData(file);

        // Verificamos que los datos se cargaron correctamente.
        RouteResponse response = routeService.findFastestRoute("CP1", "R13");

        assertNotNull(response);
        assertEquals(113, response.getTotalTime());
        assertEquals(List.of("CP1", "R11", "R12", "R13"), response.getRoute());
    }

    @Test
    void testLoadDataWithInvalidLine() throws IOException {
        String csvContent = "CP1;R11;84\nInvalidLine\nR11;R12;20";
        MockMultipartFile file = new MockMultipartFile("data", "data.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8));

        // La prueba debe ejecutarse sin lanzar una excepción, ya que el servicio debe
        // ignorar las líneas mal formateadas.
        assertDoesNotThrow(() -> routeService.loadData(file));

        // Verificamos que la línea válida se cargó y la inválida se ignoró.
        RouteResponse response = routeService.findFastestRoute("CP1", "R12");
        assertEquals(104, response.getTotalTime());
    }

    // --- Pruebas para findFastestRoute (Algoritmo de Dijkstra) ---

    // Preparación de un escenario base para las siguientes pruebas
    private void setupGraphForDijkstra() throws IOException {
        String csvContent = "loc_start;loc_end;time\n" +
                "R11;R12;20\n" +
                "R12;R13;9\n" +
                "R13;R12;11\n" +
                "R13;R20;9\n" +
                "R20;R13;11\n" +
                "CP1;R11;84\n" +
                "R11;CP1;92\n" +
                "CP1;CP2;7\n" +
                "CP2;CP1;10\n" +
                "CP2;R20;67\n" +
                "R20;CP2;60";
        MockMultipartFile file = new MockMultipartFile("data", "data.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8));
        routeService.loadData(file);
    }

    @Test
    void testFindFastestRouteExists() throws IOException {
        setupGraphForDijkstra();

        RouteResponse response = routeService.findFastestRoute("CP1", "R20");

        assertNotNull(response);
        assertEquals(74, response.getTotalTime());
        assertEquals(List.of("CP1", "CP2", "R20"), response.getRoute());
    }

    @Test
    void testFindFastestRouteWithCycle() throws IOException {
        setupGraphForDijkstra();

        // Probamos una ruta que pasa por un ciclo, pero Dijkstra debería encontrar la más corta.
        RouteResponse response = routeService.findFastestRoute("CP1", "R13");

        assertNotNull(response);
        // CP1 -> CP2 (7) -> R20 (67) -> R13 (11) = 85
        // CP1 -> R11 (84) -> R12 (20) -> R13 (9) = 113
        assertEquals(85, response.getTotalTime());
        assertEquals(List.of("CP1", "CP2", "R20", "R13"), response.getRoute());
    }

    @Test
    void testFindFastestRouteToSelf() throws IOException {
        setupGraphForDijkstra();

        RouteResponse response = routeService.findFastestRoute("CP1", "CP1");

        assertNotNull(response);
        assertEquals(0, response.getTotalTime());
        assertEquals(List.of("CP1"), response.getRoute());
    }

    @Test
    void testFindFastestRouteNotExists() throws IOException {
        setupGraphForDijkstra();

        // 'NonExistent' no existe en el grafo.
        RouteResponse response = routeService.findFastestRoute("CP1", "NonExistent");

        assertNotNull(response);
        assertEquals(-1, response.getTotalTime());
        assertTrue(response.getRoute().isEmpty());
    }

    @Test
    void testFindFastestRouteNoPathExists() throws IOException {
        // Creamos un grafo desconectado
        String csvContent = "loc_start;loc_end;time\nA;B;10\nC;D;20";
        MockMultipartFile file = new MockMultipartFile("data", "data.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8));
        routeService.loadData(file);

        // No hay camino entre A y C
        RouteResponse response = routeService.findFastestRoute("A", "C");

        assertNotNull(response);
        assertEquals(-1, response.getTotalTime());
        assertTrue(response.getRoute().isEmpty());
    }
}
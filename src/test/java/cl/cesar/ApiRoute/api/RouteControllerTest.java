package cl.cesar.ApiRoute.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.cesar.ApiRoute.service.RouteService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RouteController.class)
public class RouteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RouteService routeService;

    @Autowired
    private ObjectMapper objectMapper; // Utilizado para convertir objetos a JSON

    // --- Pruebas para el endpoint POST /api/routes/load ---

    @Test
    void testLoadData_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "data.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "CP1;R11;84".getBytes()
        );

        // Configuramos el mock para no hacer nada cuando se llama a loadData
        doNothing().when(routeService).loadData(any());

        mockMvc.perform(multipart("/api/routes/load").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("Datos de rutas cargados correctamente."));

        // Verificamos que el método del servicio fue llamado una vez
        verify(routeService, times(1)).loadData(any());
    }

    @Test
    void testLoadData_EmptyFile() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.csv", MediaType.TEXT_PLAIN_VALUE, new byte[0]);

        mockMvc.perform(multipart("/api/routes/load").file(emptyFile))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Por favor, selecciona un archivo para cargar."));

        // Verificamos que el método del servicio nunca fue llamado
        verify(routeService, never()).loadData(any());
    }

    @Test
    void testLoadData_InternalError() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "data.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "CP1;R11;84".getBytes()
        );

        // Configuramos el mock para lanzar una excepción de E/S
        doThrow(new IOException("Error de prueba")).when(routeService).loadData(any());

        mockMvc.perform(multipart("/api/routes/load").file(file))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error al procesar el archivo CSV: Error de prueba"));
    }

    // --- Pruebas para el endpoint GET /api/routes/find ---

    @Test
    void testFindFastestRoute_RouteFound() throws Exception {
        RouteResponse mockResponse = new RouteResponse(List.of("CP1", "CP2", "R20"), 74);

        // Configuramos el mock para devolver una respuesta válida
        when(routeService.findFastestRoute("CP1", "R20")).thenReturn(mockResponse);

        mockMvc.perform(get("/api/routes/find")
                .param("origin", "CP1")
                .param("destination", "R20")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.route").isArray())
                .andExpect(jsonPath("$.route[0]").value("CP1"))
                .andExpect(jsonPath("$.totalTime").value(74));

        // Verificamos que el método del servicio fue llamado con los parámetros correctos
        verify(routeService, times(1)).findFastestRoute("CP1", "R20");
    }

    @Test
    void testFindFastestRoute_RouteNotFound() throws Exception {
        RouteResponse mockResponse = new RouteResponse(List.of(), -1);

        // Configuramos el mock para devolver una respuesta de "no encontrado"
        when(routeService.findFastestRoute("CP1", "NonExistent")).thenReturn(mockResponse);

        mockMvc.perform(get("/api/routes/find")
                .param("origin", "CP1")
                .param("destination", "NonExistent")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.totalTime").value(-1));
    }
}
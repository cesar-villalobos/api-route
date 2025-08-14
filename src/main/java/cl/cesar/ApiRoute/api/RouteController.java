package cl.cesar.ApiRoute.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import cl.cesar.ApiRoute.service.RouteService;

import java.io.IOException;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    @Autowired
    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    /**
     * Endpoint para cargar datos de conexiones a través de un archivo CSV.
     * Recibe un archivo y utiliza el RouteService para construir el grafo.
     *
     * Ejemplo de uso:
     * POST /api/routes/load
     * Con un archivo CSV adjunto llamado "file".
     */
    @PostMapping("/load")
    public ResponseEntity<String> loadData(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Por favor, selecciona un archivo para cargar.");
        }
        try {
            routeService.loadData(file);
            return ResponseEntity.ok("Datos de rutas cargados correctamente.");
        } catch (IOException e) {
            // Manejo de errores en caso de problemas al leer el archivo
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar el archivo CSV: " + e.getMessage());
        }
    }

    /**
     * Endpoint para encontrar la ruta más rápida entre dos ubicaciones.
     * Recibe el origen y el destino como parámetros de consulta.
     *
     * Ejemplo de uso:
     * GET /api/routes/find?origin=CP1&destination=R20
     */
    @GetMapping("/find")
    public ResponseEntity<RouteResponse> findFastestRoute(
            @RequestParam String origin,
            @RequestParam String destination) {

        RouteResponse response = routeService.findFastestRoute(origin, destination);

        if (response.getTotalTime() == -1) {
            // Si el tiempo es -1, significa que no se encontró una ruta
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new RouteResponse(null, -1));
        }

        return ResponseEntity.ok(response);
    }
}
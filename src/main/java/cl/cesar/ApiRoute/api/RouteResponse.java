package cl.cesar.ApiRoute.api;

import java.util.List;

/**
 * DTO (Data Transfer Object) para la respuesta de la API.
 * Contiene la ruta más rápida y el tiempo total de viaje.
 */
public class RouteResponse {

    private List<String> route;
    private int totalTime;

    public RouteResponse(List<String> route, int totalTime) {
        this.route = route;
        this.totalTime = totalTime;
    }

    public List<String> getRoute() {
        return route;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setRoute(List<String> route) {
        this.route = route;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }
}

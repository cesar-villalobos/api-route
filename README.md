# API de Rutas Más Rápidas

## Descripción

API REST desarrollada en Java 21 y Spring Boot que encuentra la ruta más rápida entre dos ubicaciones dado un conjunto de conexiones con sus tiempos de viaje. Utiliza el algoritmo de Dijkstra para garantizar que se encuentra la ruta óptima.

## Características Principales

- **Carga de datos**: Endpoint para cargar tiempos de viaje desde archivos CSV
- **Cálculo de rutas**: Encuentra la ruta más rápida entre dos ubicaciones
- **Alto rendimiento**: Responde en menos de 300ms por consulta
- **Escalable**: Maneja hasta 10,000+ conexiones sin degradación significativa
- **Algoritmo Dijkstra**: Garantiza la ruta óptima en grafos con pesos positivos
- **Pruebas completas**: Test unitarios y de rendimiento
- **Docker**: Imagen Docker incluida para fácil despliegue

## Tecnologías Utilizadas

- **Java 21**: Última versión LTS de Java
- **Spring Boot 3.2.6**: Framework principal
- **Spring Web**: Para los endpoints REST
- **Spring Security**: Configuración básica de seguridad
- **Maven**: Gestión de dependencias y build
- **JUnit 5**: Testing
- **Mockito**: Mocking para pruebas
- **Docker**: Containerización

## Instalación y Ejecución

### Prerrequisitos
- Java 21 o superior
- Maven 3.6+
- Docker (opcional)

### Ejecutar localmente

1. **Clonar el repositorio**
```bash
git clone <repository-url>
cd api-route
```

2. **Compilar el proyecto**
```bash
./mvnw clean compile
```

3. **Ejecutar tests**
```bash
./mvnw test
```

4. **Ejecutar la aplicación**
```bash
./mvnw spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`

### Usando Docker (Recomendado)

1. **Construir la imagen**
```bash
docker build -t api-route .
```

2. **Ejecutar el contenedor**
```bash
docker run -p 8080:8080 api-route
```

## API Endpoints

### 1. Cargar Datos de Conexiones

**POST** `/api/routes/load`

Carga las conexiones entre ubicaciones desde un archivo CSV.

**Parámetros:**
- `file`: Archivo CSV con formato `loc_start;loc_end;time`

**Ejemplo de archivo CSV:**
```csv
R11;R12;20
R12;R13;9
R13;R12;11
R13;R20;9
R20;R13;11
CP1;R11;84
R11;CP1;92
CP1;CP2;7
CP2;CP1;10
CP2;R20;67
R20;CP2;60
```

**Respuesta exitosa:**
```
200 OK
"Datos de rutas cargados correctamente."
```

**Ejemplo de uso con curl:**
```bash
curl -X POST -F "file=@data.csv" http://localhost:8080/api/routes/load
```

### 2. Encontrar Ruta Más Rápida

**GET** `/api/routes/find?origin={origen}&destination={destino}`

Calcula la ruta más rápida entre dos ubicaciones.

**Parámetros:**
- `origin`: Ubicación de origen
- `destination`: Ubicación de destino

**Respuesta exitosa:**
```json
{
  "ruta": ["CP1", "CP2", "R20"],
  "tiempoTotal": 74
}
```

**Respuesta cuando no hay ruta:**
```json
{
  "ruta": [],
  "tiempoTotal": -1
}
```
Status: `404 NOT FOUND`

**Ejemplo de uso con curl:**
```bash
curl "http://localhost:8080/api/routes/find?origin=CP1&destination=R20"
```

## Algoritmo y Rendimiento

### Algoritmo de Dijkstra

La API utiliza el algoritmo de Dijkstra para encontrar la ruta más corta:

1. **Inicialización**: Establece el tiempo a cada nodo como infinito, excepto el origen (0)
2. **Cola de prioridad**: Utiliza una cola de prioridad para seleccionar siempre el nodo con menor tiempo acumulado
3. **Relajación**: Para cada vecino, calcula si el nuevo camino es más rápido
4. **Reconstrucción**: Utiliza un mapa de predecesores para reconstruir la ruta

### Complejidad Temporal
- **Tiempo**: O((V + E) log V) donde V = ubicaciones y E = conexiones
- **Espacio**: O(V + E) para almacenar el grafo

### Garantías de Rendimiento

- **< 300ms** por consulta de ruta (según especificación)
- **10,000+ conexiones** sin degradación significativa
- **Carga de datos eficiente** con HashMap para O(1) lookups

## Testing

### Tests Unitarios

```bash
# Ejecutar todos los tests
./mvnw test

# Solo tests del servicio
./mvnw test -Dtest=RouteServiceTest

# Solo tests del controlador
./mvnw test -Dtest=RouteControllerTest
```

### Tests de Rendimiento

Se incluyen tests específicos que verifican:

1. **Manejo de archivos grandes**: 10,000+ conexiones
2. **Tiempo de respuesta**: < 300ms por consulta
3. **Memoria**: Uso eficiente de estructuras de datos

```bash
# Ejecutar tests de rendimiento
./mvnw test -Dtest=PerformanceTest
```

## Solución del Desafío

Esta implementación cumple completamente con todos los requisitos del desafío técnico:

**Desarrollo en Java 21 + Spring Boot**  
**API REST que encuentra la ruta más rápida**  
**Carga de datos desde archivo CSV**  
**Endpoint para cargar tiempos de viaje**  
**Endpoint para consultar rutas**  
**Respuesta en formato especificado: {"ruta": [...], "tiempoTotal": x}**  
**Origen y destino incluidos en respuesta**  
**Tiempo de respuesta < 300ms**  
**Soporte para archivos de 10,000+ filas**  
**Estructuras de datos eficientes (HashMap, PriorityQueue)**  
**Código limpio y mantenible**  
**Pruebas unitarias completas**  
**Imagen Docker incluida**

## Licencia

Este proyecto está bajo la Licencia MIT.

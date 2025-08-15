import csv
import random

# Rutina para generar archivo csv con muchas ubicaciones 
# Para ejecutar en una máquina con python instalado usar el comando "python generate_csv.py"
# con eso se generará un archivo csv con 10000 conexiones aleatorias para probar rendimiento del api

def generate_large_csv(num_rows, num_locations):
    locations = [f"LOC{i}" for i in range(1, num_locations + 1)]
    with open('data_grande.csv', 'w', newline='') as file:
        writer = csv.writer(file, delimiter=';')
        writer.writerow(['loc_start', 'loc_end', 'time'])
        for _ in range(num_rows):
            loc_start = random.choice(locations)
            loc_end = random.choice(locations)
            # Asegúrate de que no haya conexiones a sí mismo y que no haya duplicados
            while loc_end == loc_start:
                loc_end = random.choice(locations)
            time = random.randint(1, 100)
            writer.writerow([loc_start, loc_end, time])

# Genera un archivo con 10,000 filas y 1,000 ubicaciones
generate_large_csv(10000, 1000)

print("Archivo 'data_grande.csv' generado con 10,000 filas.")
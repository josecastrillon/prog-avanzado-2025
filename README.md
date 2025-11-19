
1. Introducción
Titulo: Microservicio de notas y etiquetas

Objetivo: descomposicion de responsabilidades y la comunicacion sincorna entre servicios

2. Diseño de la Arquitectura
Componentes (Microservicios):
    2.1 Notes Service (Puerto 5000):
        Responsabilidad: "Dueño" de los datos principales de las notas (título, cuerpo).
        Tecnología: Flask.
        Funciones Clave: Crear, leer y actualizar notas.
        Comunicación Saliente: Llama al Tags Service para adjuntar etiquetas.
    2.2 Tags Service (Puerto 5001):
        Responsabilidad: Exclusivamente gestionar la asociación de etiquetas a los IDs de notas.
        Tecnología: Flask.
        Funciones Clave: Recibir solicitudes de asignación de etiquetas y devolver etiquetas por ID de nota.

3. Demostración de Comunicación
    Principio Clave: Comunicación síncrona mediante HTTP.
    Paso 1: El cliente envía una solicitud POST al Notes Service (Puerto 5000).
    Paso 2: El Notes Service guarda los datos de la nota internamente.
    Paso 3: El Notes Service actúa como cliente HTTP y utiliza la librería requests para enviar una solicitud POST interna al Tags Service (Puerto 5001) para asociar las etiquetas.
    Paso 4: El Tags Service procesa y almacena la información de las etiquetas.

4. Principios de microservicios
    Aislamiento de Responsabilidades (SRP): Cada servicio tiene una única razón para cambiar (uno solo se enfoca en Notas, el otro solo en Etiquetas).
    Despliegue Independiente: Se demostró que cada servicio puede iniciarse y ejecutarse en su propio proceso y puerto.


Guia de ejecución:
1. Crear y Activar Entorno Virtual:
    python3 -m venv venv
    source venv/bin/activate
2. Instalar Dependencias:
    pip install -r notes_service/requirements.txt
    pip install -r tags_service/requirements.txt
3. Ejecutar Servicios:
    python notes_service/app.py
    python tags_service/app.py
4. Crear Notas:
    curl -X POST -H "Content-Type: application/json" -d {
    "title": "nota de prueba",
    "body": "Iniciar proyecto flask.",
    "tags": ["urgente", "flask", "tarea"] 
    }' http://127.0.0.1:5000/notes
5. demostrar independente de Tags Service:
# Reemplaza [ID_DE_LA_NOTA] con el ID devuelto por el POST anterior
curl -X GET [http://127.0.0.1:5001/tags/](http://127.0.0.1:5001/tags/)[ID_DE_LA_NOTA]
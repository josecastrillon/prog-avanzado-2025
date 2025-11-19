from flask import Flask, request, jsonify
import requests # Necesario para llamar al Tags Service
import uuid     # Para generar IDs únicos para las notas

# --- Configuración ---
app = Flask(__name__)
# Diccionario para simular una base de datos de notas: { id: { 'title': '...', 'body': '...' } }
NOTES = {}
TAGS_SERVICE_URL = "http://127.0.0.1:5001"  # Dirección del Tags Service

# --- Endpoints ---

@app.route('/notes', methods=['POST'])
def create_note():
    """Crea una nueva nota y llama al Tags Service para manejar las etiquetas."""
    
    data = request.get_json()
    if not data or 'title' not in data or 'body' not in data:
        return jsonify({"error": "Missing title or body"}), 400

    # 1. Crear y guardar la nota en el servicio de notas
    note_id = str(uuid.uuid4())
    title = data['title']
    body = data['body']
    tags = data.get('tags', []) # Obtiene las etiquetas, si existen
    
    NOTES[note_id] = {'id': note_id, 'title': title, 'body': body}
    
    # 2. Intentar comunicarse con el Tags Service (ejemplo de microservicio)
    try:
        tag_payload = {'note_id': note_id, 'tags': tags}
        # Hacemos una llamada HTTP al otro microservicio
        response = requests.post(f"{TAGS_SERVICE_URL}/tags/assign", json=tag_payload)
        
        if response.status_code == 200:
            print(f"✅ Etiquetas asignadas a nota {note_id}")
        else:
            print(f"⚠️ Error al asignar etiquetas. Status: {response.status_code}")
            # En un sistema real, aquí se pondría la tarea en una cola (Async)
            
    except requests.exceptions.ConnectionError:
        print(f"❌ Tags Service no disponible en {TAGS_SERVICE_URL}")

    return jsonify({"message": "Note created successfully", "note_id": note_id}), 201


@app.route('/notes', methods=['GET'])
def list_notes():
    """Devuelve todas las notas creadas."""
    # En un caso real, también llamaríamos al Tags Service para obtener las etiquetas
    return jsonify(list(NOTES.values()))


@app.route('/notes/<note_id>', methods=['GET'])
def get_note(note_id):
    """Obtiene una nota específica por ID."""
    note = NOTES.get(note_id)
    if not note:
        return jsonify({"error": "Note not found"}), 404
        
    return jsonify(note)


if __name__ == '__main__':
    # Ejecutamos el servicio en el puerto 5000 (el puerto por defecto de Flask)
    print("🚀 Notes Service running on port 5000")
    app.run(port=5000, debug=True)
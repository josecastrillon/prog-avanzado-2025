from flask import Flask, request, jsonify

# --- Configuración ---
app = Flask(__name__)

# Diccionario para simular la base de datos de etiquetas:
# { 'note_id': [ 'tag1', 'tag2', ... ] }
NOTE_TAGS_MAP = {}

# --- Endpoints ---

@app.route('/tags/assign', methods=['POST'])
def assign_tags():
    """Recibe un ID de nota y una lista de etiquetas para asociarlas."""
    data = request.get_json()
    
    # 1. Validación simple
    note_id = data.get('note_id')
    tags = data.get('tags', [])
    
    if not note_id:
        return jsonify({"error": "Missing note_id"}), 400

    # 2. Guardar las etiquetas asociadas
    NOTE_TAGS_MAP[note_id] = tags
    print(f"✅ Etiquetas '{tags}' asignadas a la nota {note_id}. Estado actual: {NOTE_TAGS_MAP}")
    
    return jsonify({"message": "Tags assigned successfully"}), 200


@app.route('/tags/<note_id>', methods=['GET'])
def get_tags_for_note(note_id):
    """Devuelve las etiquetas para una nota específica."""
    tags = NOTE_TAGS_MAP.get(note_id, [])
    
    return jsonify({"note_id": note_id, "tags": tags}), 200

# Endpoint de salud para verificación (opcional pero recomendado en microservicios)
@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({"status": "Tags Service running"}), 200


if __name__ == '__main__':
    # Es VITAL que este servicio corra en un puerto diferente (5001)
    print("🚀 Tags Service running on port 5001")
    app.run(port=5001, debug=True)
from flask import Flask, jsonify, request

app = Flask(__name__)

services = [
    {
        "id": "1",
        "name": "garantia",
        "description": "Servicio de garantía extendida",
        "date": "2020-01-01",
        "cost": "0"
    },
    {
        "id": "2",
        "name": "mantenimiento",
        "description": "Servicio de mantenimiento anual",
        "date": "2021-05-10",
        "cost": "150"
    },
    {
        "id": "3",
        "name": "instalacion",
        "description": "Servicio de instalación a domicilio",
        "date": "2022-02-20",
        "cost": "50"
    }
]


@app.route("/")
def root():
    return "Hello World!"



@app.route("/services", methods=["GET"])
def get_all_services():
    query = request.args.get('query')
    if query:
      
        filtered = [s for s in services if query.lower() in s["name"].lower()]
        return jsonify(filtered), 200
    return jsonify(services), 200



@app.route("/service/<service_id>", methods=["GET"])
def get_service(service_id):
    service = next((s for s in services if s["id"] == service_id), None)
    if service:
        query = request.args.get('query')
        if query:
            service['query'] = query
        return jsonify(service), 200
    return jsonify({"error": "Servicio no encontrado"}), 404


if __name__ == '__main__':
    app.run(debug=True)

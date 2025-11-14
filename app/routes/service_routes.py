from flask import Blueprint, jsonify
from app.controllers.service_controller import get_all_services, get_service_by_id

service_bp = Blueprint('service_bp', __name__)

@service_bp.route('/services', methods=['GET'])
def get_services():
    return jsonify(get_all_services())

@service_bp.route('/services/<int:service_id>', methods=['GET'])
def get_service(service_id):
    service = get_service_by_id(service_id)
    if service:
        return jsonify(service)
    return jsonify({'error': 'Servicio no encontrado'}), 404

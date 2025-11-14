from flask import Blueprint, jsonify
from app.controllers.user_controller import get_all_users, get_user_by_id

user_bp = Blueprint('user_bp', __name__)

@user_bp.route('/users', methods=['GET'])
def get_users():
    return jsonify(get_all_users())

@user_bp.route('/users/<int:user_id>', methods=['GET'])
def get_user(user_id):
    user = get_user_by_id(user_id)
    if user:
        return jsonify(user)
    return jsonify({'error': 'Usuario no encontrado'}), 404

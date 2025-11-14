from flask import Blueprint, jsonify
from app.controllers.product_controller import get_all_products, get_product_by_id

product_bp = Blueprint('product_bp', __name__)

@product_bp.route('/products', methods=['GET'])
def get_products():
    return jsonify(get_all_products())

@product_bp.route('/products/<int:product_id>', methods=['GET'])
def get_product(product_id):
    product = get_product_by_id(product_id)
    if product:
        return jsonify(product)
    return jsonify({'error': 'Producto no encontrado'}), 404

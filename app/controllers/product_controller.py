from app.services.product_service import load_products

def get_all_products():
    return load_products()

def get_product_by_id(product_id):
    products = load_products()
    return next((p for p in products if p["id"] == product_id), None)

import json, os

def load_products():
    path = os.path.join(os.path.dirname(__file__), '..', 'data', 'products.json')
    with open(path, 'r', encoding='utf-8') as f:
        data = json.load(f)
    return data#["products"]

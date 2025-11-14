import json, os

def load_services():
    path = os.path.join(os.path.dirname(__file__), '..', 'data', 'services.json')
    with open(path, 'r', encoding='utf-8') as f:
        data = json.load(f)
    return data#["services"]

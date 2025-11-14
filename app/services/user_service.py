import json, os

def load_users():
    path = os.path.join(os.path.dirname(__file__), '..', 'data', 'users.json')
    with open(path, 'r', encoding='utf-8') as f:
        data = json.load(f)
    return data#["users"]
def get_user_by_id(user_id):
    users = load_users()
    for user in users:
        if user["id"] == user_id:
            return user
    return None


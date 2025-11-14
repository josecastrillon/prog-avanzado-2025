from app.services.user_service import load_users

def get_all_users():
    return load_users()

def get_user_by_id(user_id):
    users = load_users()
    return next((u for u in users if u["id"] == user_id), None)

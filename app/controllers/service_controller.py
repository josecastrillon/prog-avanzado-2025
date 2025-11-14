from app.services.service_service import load_services

def get_all_services():
    return load_services()

def get_service_by_id(service_id):
    services = load_services()
    return next((s for s in services if s["id"] == service_id), None)

#Ejecutar en powerSell
#python app.py  
from flask import Flask

app = Flask(__name__)

@app.route('/')
def root():
    return "Bienvenido a la página principal de tienda en línea!";

# Importar los Blueprints
from app.routes.user_routes import user_bp
from app.routes.product_routes import product_bp
from app.routes.service_routes import service_bp

# Registrar los Blueprints
app.register_blueprint(user_bp)
app.register_blueprint(product_bp)
app.register_blueprint(service_bp)

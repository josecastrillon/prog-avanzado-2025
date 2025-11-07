package co.edu.uniremington.library.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para documentación de la API REST.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configura la información general de la API.
     *
     * @return Objeto OpenAPI configurado
     */
    @Bean
    public OpenAPI libraryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Library Management System API")
                        .description("""
                                API REST para gestión de biblioteca digital.
                                
                                ## Funcionalidades:
                                - **Libros**: Gestión de inventario de libros
                                - **Usuarios**: Administración de usuarios de la biblioteca
                                - **Préstamos**: Control de préstamos y devoluciones
                                
                                ## Reglas de Negocio:
                                - Los usuarios con multas no pueden pedir libros prestados
                                - Solo se pueden prestar libros con copias disponibles
                                - No se puede devolver un préstamo ya devuelto
                                
                                ## Endpoints Principales:
                                - `/api/books` - Gestión de libros
                                - `/api/users` - Gestión de usuarios
                                - `/api/loans` - Gestión de préstamos
                                
                                ## Proyecto Educativo:
                                Este proyecto está diseñado para enseñar **Testing con JUnit y Mockito**.
                                Los principios SOLID se aplican consistentemente en toda la arquitectura.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Universidad Remington")
                                .email("biblioteca@uniremington.edu.co")
                                .url("https://uniremington.edu.co"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desarrollo"),
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Testing")
                ));
    }
}

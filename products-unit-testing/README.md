# Proyecto Base - Examen de Pruebas Unitarias

## Descripción del Proyecto

Este es un proyecto Maven con Java 21 y Spring Boot que implementa un **sistema de gestión de productos** con arquitectura en capas. El proyecto está diseñado para practicar y evaluar pruebas unitarias utilizando JUnit 5 y Mockito.

### ¿Qué hace este proyecto?

El sistema permite realizar operaciones CRUD sobre productos:
- **Crear** un nuevo producto
- **Buscar** un producto por su ID
- **Listar** todos los productos
- **Actualizar** el precio de un producto
- **Validaciones** de negocio (nombre obligatorio, precio positivo)

### Arquitectura

El proyecto sigue una arquitectura en capas siguiendo principios SOLID:

```
Controller → Service → Repository
```

- **Controller**: Maneja las peticiones HTTP (REST API)
- **Service**: Contiene la lógica de negocio y validaciones
- **Repository**: Gestiona el almacenamiento en memoria (sin base de datos)
- **Model**: Entidades del dominio (Product)
- **Exception**: Excepciones personalizadas del dominio

**Paquete base:** `co.edu.uniremington.products`

---

## 📋 INSTRUCCIONES DEL EXAMEN

### Objetivo

Implementar pruebas unitarias completas ÚNICAMENTE para:

1. ✅ **ProductServiceImpl** (capa de servicios - lógica de negocio)
2. ✅ **ProductController** (capa de controladores - endpoints REST)

### ⚠️ IMPORTANTE: ¿Qué se debe testear?

**SÍ se deben hacer tests para:**
- ✅ `ProductServiceImpl` (service/impl)
- ✅ `ProductController` (controller)

**NO se deben hacer tests para:**
- ❌ `Product` (modelo/POJO)
- ❌ `ProductRepository` (repositorio en memoria, sin lógica de negocio)
- ❌ `ProductNotFoundException` y `InvalidPriceException` (excepciones simples)
- ❌ `Application` (clase main de Spring Boot)

### Archivos a Completar

Debes implementar los tests en estos dos archivos:

1. **`src/test/java/co/edu/uniremington/products/service/ProductServiceImplTest.java`**
   - Testear método `createProduct()` (caso exitoso + validaciones)
   - Testear método `findById()` (caso exitoso + producto no encontrado)
   - Testear método `findAll()` (verificar llamada al repositorio)
   - Testear método `updatePrice()` (caso exitoso + validaciones)

2. **`src/test/java/co/edu/uniremington/products/controller/ProductControllerTest.java`**
   - Testear POST `/api/products` (creación exitosa con status 201)
   - Testear GET `/api/products/{id}` (obtener producto existente)
   - Testear GET `/api/products` (listar todos los productos)
   - Testear PATCH `/api/products/{id}/price` (actualizar precio)

---

## 🛠️ Tecnologías

- **Java 21**
- **Spring Boot 3.2.0**
- **Maven**
- **JUnit 5** (Jupiter)
- **Mockito** (incluido en spring-boot-starter-test)
- **Jacoco** (cobertura de código)

---

## 🚀 Trabajar con IntelliJ IDEA (Recomendado)

### Configuración Inicial

1. Abrir el proyecto en IntelliJ IDEA
2. Configurar el Project SDK:
   - `File → Project Structure → Project`
   - SDK: Java 21 (`C:\Java\jdk21.0.4_7`)
3. Esperar a que IntelliJ indexe el proyecto y descargue dependencias

### Ejecutar Tests

- **Ejecutar un test individual**: Click derecho en el método `@Test` → Run
- **Ejecutar toda la clase de test**: Click derecho en la clase → Run
- **Ver cobertura**: Click derecho → Run with Coverage

---

## 📊 Jacoco - Reporte de Cobertura

### Generar Reporte de Cobertura

Ejecuta en la terminal de IntelliJ o en línea de comandos:

```bash
mvn clean test
```

El reporte HTML se genera automáticamente en:
```
target/site/jacoco/index.html
```

Abre este archivo en tu navegador para ver:
- Porcentaje de cobertura por clase
- Líneas cubiertas vs. no cubiertas
- Cobertura de branches (condicionales)

### Ver Reporte

```bash
# Windows - Abrir el reporte desde línea de comandos
start target/site/jacoco/index.html
```

O navega manualmente a la carpeta `target/site/jacoco/` y abre `index.html`

### Meta de Cobertura

El proyecto está configurado para requerir **mínimo 80% de cobertura** en las clases de Service y Controller.

**Nota:** Jacoco ya está configurado para excluir automáticamente:
- Clase `Application`
- Modelos (POJOs)
- Excepciones
- Repository

---

## 📝 Comandos Maven (Opcional - Línea de Comandos)

Si prefieres trabajar desde la línea de comandos:

```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar tests
mvn test

# Generar reporte de cobertura
mvn test jacoco:report

# Verificar cobertura mínima (80%)
mvn verify
```

O usa los scripts batch incluidos:
```bash
compile.bat  # Compilar
test.bat     # Ejecutar tests
run.bat      # Ejecutar la aplicación
```

---

## 🎯 Criterios de Evaluación

### Técnicas de Testing

1. **Uso correcto de Mockito** (40%)
   - Uso de `@Mock` para crear mocks
   - Uso de `@InjectMocks` para inyectar dependencias
   - Configuración con `when().thenReturn()`
   - Verificación con `verify()`

2. **Cobertura de casos** (30%)
   - Casos exitosos (happy path)
   - Casos de error y validaciones
   - Manejo de excepciones

3. **Assertions apropiadas** (20%)
   - Uso de AssertJ o JUnit assertions
   - Verificación de valores esperados
   - Verificación de excepciones

4. **Organización y buenas prácticas** (10%)
   - Nombres descriptivos de tests
   - Patrón AAA (Arrange-Act-Assert)
   - Tests independientes

### Ejemplos de lo que se evaluará

**En ProductServiceImplTest:**
- ¿Se mockea correctamente el `ProductRepository`?
- ¿Se verifica que el repositorio sea llamado?
- ¿Se valida que se lancen las excepciones correctas?

**En ProductControllerTest:**
- ¿Se mockea correctamente el `ProductService`?
- ¿Se verifican los códigos HTTP (200, 201)?
- ¿Se valida el contenido de las respuestas?

---

## 🌐 API Endpoints (Para Testing)

### Crear Producto
```http
POST /api/products
Content-Type: application/json

{
  "name": "Keyboard",
  "description": "Mechanical Keyboard",
  "price": 89.99,
  "stock": 15
}
```
**Respuesta:** 201 CREATED

### Obtener Producto por ID
```http
GET /api/products/{id}
```
**Respuesta:** 200 OK (si existe) o Exception (si no existe)

### Listar Todos los Productos
```http
GET /api/products
```
**Respuesta:** 200 OK con lista de productos

### Actualizar Precio
```http
PATCH /api/products/{id}/price?price=99.99
```
**Respuesta:** 200 OK con producto actualizado

---

## 📚 Recursos y Referencias

### Documentación útil

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ Documentation](https://assertj.github.io/doc/)

### Ejemplo de estructura de un test

```java
@Test
void shouldCreateProductSuccessfully() {
    // Arrange (preparar)
    Product product = new Product(null, "Laptop", "HP", new BigDecimal("1200"), 10);

    // Act (actuar)
    Product result = productService.createProduct(product);

    // Assert (verificar)
    assertNotNull(result);
    assertEquals("Laptop", result.getName());
}
```

---

## ✅ Checklist para el Examen

- [ ] ProductServiceImplTest - Test de createProduct (caso exitoso)
- [ ] ProductServiceImplTest - Test de createProduct (validaciones)
- [ ] ProductServiceImplTest - Test de findById (caso exitoso)
- [ ] ProductServiceImplTest - Test de findById (producto no encontrado)
- [ ] ProductServiceImplTest - Test de findAll
- [ ] ProductServiceImplTest - Test de updatePrice (caso exitoso)
- [ ] ProductServiceImplTest - Test de updatePrice (validaciones)
- [ ] ProductControllerTest - Test de POST /api/products
- [ ] ProductControllerTest - Test de GET /api/products/{id}
- [ ] ProductControllerTest - Test de GET /api/products
- [ ] ProductControllerTest - Test de PATCH /api/products/{id}/price
- [ ] Generar reporte de Jacoco
- [ ] Verificar cobertura mínima 80%

---

## 🏃 Ejecutar la Aplicación (Opcional)

Si deseas probar los endpoints manualmente:

```bash
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

Puedes usar Postman, cURL o cualquier cliente HTTP para probar los endpoints.

---

## 💡 Consejos

1. **Lee el código existente** antes de empezar a escribir tests
2. **Empieza por los tests más simples** y ve aumentando la complejidad
3. **Usa nombres descriptivos** para tus métodos de test
4. **Ejecuta los tests frecuentemente** para verificar que funcionan
5. **Revisa el reporte de Jacoco** para identificar código no cubierto
6. **No testees métodos privados** directamente, se cubren indirectamente

---

## ❓ Preguntas Frecuentes

**P: ¿Debo testear el ProductRepository?**
R: No, el repository es una implementación simple en memoria sin lógica de negocio. Se excluye del examen.

**P: ¿Debo testear las clases de modelo (Product)?**
R: No, los POJOs no requieren tests unitarios en este examen.

**P: ¿Puedo usar spring-boot-starter-test?**
R: Sí, ya está incluido. Contiene JUnit 5, Mockito, y otras librerías útiles.

**P: ¿Cómo mockeo el repository en ProductServiceImplTest?**
R: Usa `@Mock` para ProductRepository y `@InjectMocks` para ProductServiceImpl.

**P: ¿Necesito MockMvc para testear el controller?**
R: Puedes usar MockMvc o simplemente mockear el service y llamar directamente los métodos del controller.

---

**¡Buena suerte con el examen!** 🚀

# 🔧 Examen Final: AutoFix Microservices

## 🛑 Situación Crítica
Has sido contratado como **Ingeniero de Software** en "AutoFix". El sistema de microservicios entregado por el proveedor anterior está **inoperable**.

Tu responsabilidad es diagnosticar la arquitectura, reparar la comunicación entre servicios y asegurar la calidad del código mediante pruebas unitarias.

### 🏗️ Arquitectura del Sistema
* **Discovery Server (8761):** Registro de servicios.
* **API Gateway (9000):** Puerta de entrada única.
* **Vehicle Service (8081):** Lógica de negocio (Vehículos).
* **Repair Service (8082):** Lógica de negocio (Reparaciones).

---

## 📋 INSTRUCCIONES DEL EXAMEN

**Tiempo:** 120 Minutos

### 🔍 FASE 1: Diagnóstico y Reparación (60%)

**El Problema:** El sistema no conecta. Los servicios están aislados y el Gateway no responde correctamente.

**Reporte de Errores (Síntomas Observados):**
1.  **Eureka Dashboard:** Al iniciar todo, algunos microservicios **NO** aparecen en la lista de registrados.
2.  **Gateway Error:** Al consumir el endpoint `/api/repairs`, el servidor responde con **Error 500 (Internal Server Error)**.
3.  **Gateway Error:** Al consumir el endpoint `/api/vehicles...`, el servidor responde con **Error 404 (Not Found)**.

**Su Misión:**
Investigue los archivos de configuración (`yml`, `properties`) y dependencias (`pom.xml`). Encuentre la causa raíz de estos 3 síntomas y soluciónelos.

**Criterio de Aceptación:**
* Eureka muestra 3 servicios `UP`.
* Postman recibe respuesta `200 OK` de ambos endpoints a través del puerto `9000`.

---

### 🧪 FASE 2: Lógica y Calidad (40%)

**El Problema:** El sistema conecta, pero los usuarios reportan que la recomendación de cambio de aceite es errónea.

**Regla de Negocio:**
> *Un vehículo requiere cambio de aceite si ha recorrido **5,000 km o más** desde su último mantenimiento.*

**Su Misión:**
1.  Ejecute el test existente en `VehicleServiceTest`.
2.  Analice por qué falla comparando el código con la regla de negocio.
3.  Corrija la lógica en la clase de servicio.
4.  **Entregable:** El test debe pasar en **Verde**.

---

### 🚨 FASE 3: Recuperación (Caso Especial)
*(Solo para estudiantes autorizados)*

**Requisito:** Demostrar dominio de **Mockito** para aislar dependencias.

**El Problema:** El cálculo de ingresos totales en `RepairService` es sospechoso.

**Su Misión:**
1.  Implemente un Test Unitario (`RepairServiceTest`) para el método `calculateTotalIncome`.
2.  **Debe usar Mocks** para simular el repositorio y devolver datos controlados.
3.  Si el test falla (el resultado no coincide con la suma matemática real), corrija el servicio.
4.  **Entregable:** Test con Mocks pasando en **Verde** y bug corregido.

---

## 🚀 Comandos de Validación

**Validar Vehículos:**
`POST http://localhost:9000/api/vehicles/check-oil`
```json
{
  "plate": "ABC-123",
  "currentMileage": 15000,
  "lastOilChangeMileage": 9000
}

(Nota: 15000 - 9000 = 6000. Debería pedir aceite si la lógica está arreglada).

2. Verificar Reparaciones (Vía Gateway):

HTTP

GET http://localhost:9000/api/repairs



✅ Checklist de Entrega
Recuerde crear una rama con su nombre, ejemplo: feature/examen2-JoseCastrillon
Antes de hacer commit al examen, verifique:

[ ] Eureka: Muestra los 3 servicios registrados correctamente.

[ ] Gateway: Redirige correctamente a /api/repairs (no da 404).

[ ] Gateway: Redirige correctamente a /api/vehicles (no da 404).

[ ] VehicleService: El Test Unitario VehicleServiceTest pasa en Verde.

[ ] (Solo Recuperación) RepairServiceTest con Mockito creado y pasando en Verde.

Resuelvo dudas y al finalizar me muestran lo que hicieron (tipo sustentación)

¡Mucha suerte, Ingenieros! 👨‍💻👩‍💻
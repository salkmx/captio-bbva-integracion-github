# Revisión técnica del proyecto y puntos de mejora

Fecha: 2026-04-17

## Resumen ejecutivo

El proyecto tiene una base funcional sólida para orquestar procesos batch (Spring Batch + schedulers), pero presenta riesgos importantes de **seguridad**, **operabilidad** y **mantenibilidad** que conviene priorizar.

### Top 5 prioridades

1. **Eliminar secretos en texto plano del repositorio** y migrarlos a variables de entorno/secret manager.
2. **Separar configuración por ambientes** (`application-dev`, `application-qa`, `application-prod`) y endurecer `application.properties` para producción.
3. **Reducir el acoplamiento del `JobsConfig`** (actualmente concentra demasiadas dependencias/beans) en configuraciones por dominio.
4. **Fortalecer trazabilidad y observabilidad** (logs estructurados, niveles por entorno, sin exponer payloads sensibles).
5. **Normalizar calidad de build** (quitar dependencias duplicadas y añadir reglas de calidad estática).

---

## Hallazgos y recomendaciones

## 1) Seguridad y gestión de secretos

### Hallazgo
Se detectan credenciales/sensibles embebidos en `application.properties` (por ejemplo `captio.api.token.client_secret`) y configuración SFTP con host/usuario en el mismo archivo versionado.

### Riesgo
- Exposición accidental de secretos.
- Rotación manual compleja y propensa a errores.
- Riesgo de fuga en forks, CI logs o backups.

### Recomendación
- Mover secretos a variables de entorno y/o secret manager (Vault, AWS Secrets Manager, GCP Secret Manager, etc.).
- Dejar en repo solo placeholders (`${CAPTIO_CLIENT_SECRET}`) y valores no sensibles.
- Rotar inmediatamente las credenciales que ya hayan estado en control de versiones.

---

## 2) Configuración operativa (entornos y logging)

### Hallazgo
El archivo principal tiene parámetros de runtime muy mezclados (batch, SMTP, SFTP, cron, APIs) y logging muy verboso (`DEBUG`/`TRACE`) por defecto.

### Riesgo
- Logs excesivos en producción.
- Posible exposición de datos sensibles en trazas HTTP si se registran headers/body.
- Dificultad para operar distintos ambientes.

### Recomendación
- Crear perfiles por ambiente:
  - `application-dev.properties`
  - `application-qa.properties`
  - `application-prod.properties`
- En `prod`: usar niveles `INFO/WARN`, desactivar logs de wire/http body.
- Mantener `DEBUG` solo para troubleshooting puntual.

---

## 3) Arquitectura y mantenibilidad

### Hallazgo
`JobsConfig` concentra una cantidad alta de dependencias y definición de jobs/steps en una sola clase.

### Riesgo
- Alta complejidad cognitiva.
- Difícil evolución y pruebas unitarias aisladas.
- Mayor probabilidad de regresiones al modificar jobs no relacionados.

### Recomendación
Dividir configuración batch por dominio/flujo, por ejemplo:
- `UsuariosJobsConfig`
- `ViajesJobsConfig`
- `GastosJobsConfig`
- `ReportesJobsConfig`

Además, definir convenciones de nombres y responsabilizar una clase por flujo funcional.

---

## 4) Dependencias y build hygiene

### Hallazgo
En `pom.xml` hay dependencia duplicada (`jackson-datatype-jsr310`) y comentarios “temporales” dentro de dependencias que deberían estabilizarse.

### Riesgo
- Señales de deuda técnica.
- Build menos limpio y potenciales conflictos de resolución.

### Recomendación
- Eliminar duplicados y ordenar dependencias.
- Añadir reglas automáticas de calidad:
  - `maven-enforcer-plugin`
  - `spotbugs` o `pmd`
  - `checkstyle`/`spotless`
- Activar pipeline CI con validaciones mínimas (`test`, estilo, análisis estático).

---

## 5) Estrategia de pruebas

### Hallazgo
Actualmente hay tests, pero faltan barreras para detectar problemas de configuración y contratos de integración antes de despliegue.

### Recomendación
- Añadir:
  - tests de configuración (`@SpringBootTest` con perfiles)
  - tests de contrato para integraciones HTTP/SFTP
  - pruebas de componentes críticos de procesamiento (readers/processors/writers)
- Definir umbral mínimo de cobertura para módulos críticos.

---

## Plan sugerido (30/60/90 días)

### 0-30 días
- Retiro de secretos del repo + rotación.
- Limpieza de `pom.xml` (duplicados).
- Perfilado de configuración por ambiente.

### 31-60 días
- Refactor de `JobsConfig` por dominios.
- Estandarización de logging y correlación de trazas.
- Incorporar quality gates en CI.

### 61-90 días
- Endurecer resiliencia (reintentos, circuit breakers, timeouts explícitos).
- Mejorar pruebas de integración y pruebas de regresión batch.
- Definir métricas operativas y SLOs.

---

## Métricas de éxito recomendadas

- **Seguridad**: 0 secretos hardcodeados detectados por escáner.
- **Operación**: reducción del volumen de logs DEBUG en producción >80%.
- **Calidad**: build CI con validaciones estáticas y test en cada PR.
- **Mantenibilidad**: reducción del tamaño de clases de configuración monolíticas.
- **Confiabilidad**: menor tasa de fallas de jobs por configuración/timeout.


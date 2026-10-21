# Microservicio de Restaurantes · Plazoleta de Comidas

API del reto Plazoleta encargada de administrar los establecimientos de la
plazoleta de comidas: sus restaurantes, la carta de platos y la contratación de
empleados. Valida los tokens JWT que emite el microservicio de usuarios con el
secreto compartido y lo consulta por Feign para confirmar propietarios y crear
cuentas de empleados. Proyecto educativo construido con **Spring Boot 4.1.1** y
arquitectura hexagonal; cubre las historias de usuario **HU2, HU3, HU4, HU6,
HU7, HU9 y HU10** del reto, además de las consultas de integración que
consume el microservicio de pedidos.

## Endpoints

| Método | Ruta | Acceso | Historia |
|---|---|---|---|
| `POST` | `/restaurantes` | Token **ADMIN** | HU2 · crea un restaurante validando al dueño contra usuarios |
| `GET` | `/restaurantes?pagina=0&cantidad=10` | Público | HU9 · listado alfabético y paginado (solo nombre y logo) |
| `GET` | `/restaurantes/{id}` | Público | Integración · ficha del restaurante con su dueño (la usa pedidos en la HU18) |
| `GET` | `/restaurantes/{id}/platos?categoria=&pagina=&cantidad=` | Público | HU10 · carta del restaurante: solo habilitados, filtrable por categoría |
| `POST` | `/platos` | Token **PROPIETARIO** | HU3 · agrega un plato a la carta del propio restaurante |
| `PUT` | `/platos/{id}` | Token **PROPIETARIO** | HU4 · corrige únicamente precio y descripción |
| `PATCH` | `/platos/{id}/disponibilidad` | Token **PROPIETARIO** | HU7 · habilita o inhabilita un plato propio |
| `GET` | `/platos/{id}` | Público | Integración · ficha del plato (la usa pedidos para validar sus órdenes) |
| `POST` | `/empleados` | Token **PROPIETARIO** | HU6 · contrata: crea la cuenta en usuarios (Feign) y guarda el vínculo |
| `GET` | `/empleados/{idUsuario}` | Autenticado | Integración · restaurante al que pertenece un empleado (lo usa pedidos) |

Las peticiones protegidas viajan con el encabezado `Authorization: Bearer <token>`.
En las llamadas Feign hacia usuarios el token del solicitante se reenvía tal cual.

## Cuentas de demostración

El microservicio de usuarios precarga con su base vacía las cuentas
`camila.rios@demo-plazoleta.co` (propietaria **id 2**) y
`jorge.barajas@demo-plazoleta.co` (propietario **id 3**), además del
administrador. Con la base de este micro vacía se crean los restaurantes
**La Brasa Dorada** (dueña id 2) y **Wok Express** (dueño id 3) con ocho
platos de muestra: seis habilitados y dos inhabilitados para ejercitar la
HU7. Los identificadores de los dueños son configurables con las variables
`SEMILLA_PROPIETARIO_BRASA` y `SEMILLA_PROPIETARIO_WOK`. Arranca primero el
microservicio de usuarios en una base nueva para que los ids coincidan; el
inicializador jamás toca tablas con datos.

## Pila tecnológica

| Componente | Detalle |
|---|---|
| Lenguaje | Java 17 |
| Framework | Spring Boot 4.1.1 (Web MVC, Data JPA, Security, Validation) |
| Comunicación entre micros | Spring Cloud OpenFeign (2025.1.2) |
| Documentación | springdoc-openapi 3.1.1 · Swagger UI |
| Base de datos | MySQL 8 |
| Seguridad | JJWT 0.12.6 · validación del secreto compartido con usuarios |
| Build | Maven 3.9 (wrapper incluido) |

## Requisitos

- JDK 17
- MySQL 8 (contenedor Docker recomendado)
- El microservicio de usuarios corriendo en `http://localhost:8080`
- Maven 3.9 o superior, o usar el wrapper incluido (`./mvnw`)

## Base de datos

Crear el esquema en MySQL:

```powershell
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS plazoleta_restaurante;"
```

## Variables de entorno

| Variable | Uso |
|---|---|
| `DB_URL` | Cadena JDBC hacia la base de datos `plazoleta_restaurante` |
| `DB_USERNAME` | Usuario de la base de datos |
| `DB_PASSWORD` | Clave de la base de datos |
| `APP_JWT_SECRET` | Secreto **idéntico** al del microservicio de usuarios (mínimo 32 caracteres) |
| `USUARIO_API_URL` | Opcional: dirección del microservicio de usuarios (por defecto `http://localhost:8080`) |
| `SEMILLA_PROPIETARIO_BRASA` | Opcional: id del dueño demo de La Brasa Dorada (por defecto 2) |
| `SEMILLA_PROPIETARIO_WOK` | Opcional: id del dueño demo de Wok Express (por defecto 3) |

## Ejecución local

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/plazoleta_restaurante"
$env:DB_USERNAME="plazoleta"
$env:DB_PASSWORD="plazoleta"
$env:APP_JWT_SECRET="Plazoleta2026*Seguro-Compartido-Entre-Micros*OK"
.\mvnw.cmd spring-boot:run
```

La API queda disponible en el puerto `8081`.

## Ejecución con Docker

```powershell
docker build -t ms-restaurantes .
docker run -p 8081:8081 --env DB_URL="jdbc:mysql://host.docker.internal:3306/plazoleta_restaurante" --env DB_USERNAME=plazoleta --env DB_PASSWORD=plazoleta --env APP_JWT_SECRET="tu-secreto-compartido-de-32-caracteres-minimo" --env USUARIO_API_URL="http://host.docker.internal:8080" ms-restaurantes
```

## Documentación interactiva

Swagger UI disponible en: `http://localhost:8081/swagger-ui/index.html`

## Pruebas

Las pruebas unitarias usan H2 en memoria, no requieren MySQL:

```bash
./mvnw test
```

## Estructura del proyecto

```
com.plazoleta.restaurante
├── domain
│   ├── api              # contratos de casos de uso
│   ├── exception        # excepciones de negocio
│   ├── model            # Restaurante, Plato, Empleado, HojaRestaurantes
│   └── spi              # puertos de salida del dominio
├── application
│   ├── dto              # objetos de transferencia
│   └── service          # implementacion de casos de uso
└── infrastructure
    ├── configuration    # OpenAPI y semilla de datos demo
    ├── integraciones    # Feign hacia el microservicio de usuarios
    ├── persistence      # JPA: entidades, repositorios, conversores, adaptadores
    ├── rest             # controladores, conversores REST y manejo de errores
    └── security         # validacion del JWT compartido y reglas por rol
```

## Autores

- **Isaac Herrera**
- **Cesar Orozco**

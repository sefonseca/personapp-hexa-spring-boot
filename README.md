# PersonApp – Arquitectura Hexagonal con Spring Boot

Servicio web y aplicación de consola para el CRUD de **Persona, Profesión, Teléfono y Estudio**, construido con
**arquitectura hexagonal (puertos y adaptadores)**, patrones **Repository** y **Service (casos de uso)**, en un
proyecto **multimódulo Maven**. Cada operación puede ejecutarse sobre **MariaDB** o **MongoDB**, eligiendo la base
en cada petición.

> Laboratorio 2 – Arquitectura de Software – Pontificia Universidad Javeriana.
> Parte del template [andres-karoll/personapp-hexa-spring-boot](https://github.com/andres-karoll/personapp-hexa-spring-boot),
> que estaba incompleto y con errores. Lo que se encontró y cómo se corrigió está en
> [`docs/HALLAZGOS.md`](docs/HALLAZGOS.md).

## Tecnologías y versiones

| Herramienta | Versión | Observación |
|---|---|---|
| Java (JDK) | **25 LTS** | Temurin 25 |
| Spring Boot | **4.1.2** | Spring Framework 7, Hibernate 7, Jakarta EE 11 |
| Maven | 3.9.x | Multimódulo, BOM de Spring Boot importado |
| MariaDB | **12.3 LTS** | Imagen `mariadb:12.3` |
| MongoDB | **8.0** | Imagen `mongo:8.0` |
| springdoc-openapi | 3.2.0 | Swagger UI / OpenAPI 3.1 |
| Lombok | 1.18.48 | Declarado como annotation processor (obligatorio desde JDK 23) |
| Docker / Docker Compose | 24+ / v2 | Despliegue de bases + aplicaciones |

## Estructura de módulos

```
personapp-hexa-spring-boot (pom padre)
├── common                 Anotaciones (@Adapter, @UseCase, @Port, @Mapper), excepciones y DatabaseOption
├── domain                 Modelo de dominio puro: Person, Profession, Phone, Study, Gender
├── application            Puertos de entrada (in), puertos de salida (out) y casos de uso (servicios)
├── maria-output-adapter   Adaptador de salida JPA/MariaDB: entidades, repositorios, mappers, adaptadores
├── mongo-output-adapter   Adaptador de salida Spring Data MongoDB: documentos, repositorios, mappers, adaptadores
├── rest-input-adapter     Adaptador de entrada REST (puerto 3000) + Swagger      -> app ejecutable
├── cli-input-adapter      Adaptador de entrada de consola (menús)                 -> app ejecutable
└── scripts                DDL y DML de MariaDB y MongoDB + prueba de humo de la API
```

Dependencias entre módulos (la flecha indica "depende de"):

```
rest-input-adapter ─┐                 ┌─> maria-output-adapter ─┐
                    ├─> (ambos) ──────┤                         ├─> application ─> domain ─> common
cli-input-adapter ──┘                 └─> mongo-output-adapter ─┘
```

El dominio y la aplicación no conocen a Spring Data, JPA, MongoDB ni HTTP; solo conocen los puertos.

## Opción 1 – Despliegue con Docker (recomendado)

Requisitos: Docker Desktop (o Docker Engine + Compose v2). No hace falta instalar Java, Maven ni bases de datos.

```bash
git clone https://github.com/sefonseca/personapp-hexa-spring-boot.git
cd personapp-hexa-spring-boot

# Compila con Maven dentro de Docker, levanta MariaDB, MongoDB y la API REST
docker compose up -d --build

# Ver el estado (mariadb y mongodb deben quedar "healthy")
docker compose ps
docker compose logs -f rest
```

- API REST: <http://localhost:3000/api/v1/persona/MARIA>
- Swagger UI: <http://localhost:3000/swagger-ui.html>
- OpenAPI (JSON): <http://localhost:3000/api-docs>

Aplicación de consola (usa las mismas bases):

```bash
docker compose run --rm cli
```

Al crear los contenedores por primera vez se ejecutan automáticamente los scripts de `scripts/`
(`docker-entrypoint-initdb.d`). Para volver a los datos iniciales:

```bash
docker compose down -v      # -v borra los volúmenes con los datos
docker compose up -d --build
```

| Servicio | Contenedor | Puerto en el host | Credenciales |
|---|---|---|---|
| MariaDB 12.3 | personapp-mariadb | 3307 | root/root · app: persona_db/persona_db |
| MongoDB 8.0 | personapp-mongodb | 27017 | root/root · app: persona_db/persona_db (authSource=admin) |
| API REST | personapp-rest | 3000 | – |

## Opción 2 – Ejecución local (sin Docker para la app)

### 1. Requisitos

- JDK 25 (`java -version` debe mostrar 25).
- Maven 3.9+ (`mvn -version`).
- MariaDB escuchando en el puerto **3307** y MongoDB en el **27017** (instalados o solo las bases con Docker:
  `docker compose up -d mariadb mongodb`).

### 2. Crear las bases de datos

MariaDB (como root):

```bash
mariadb -uroot -p -P3307 -h127.0.0.1 < scripts/persona_ddl_maria.sql
mariadb -uroot -p -P3307 -h127.0.0.1 < scripts/persona_dml_maria.sql
```

MongoDB (como un usuario administrador, con `mongosh`):

```bash
mongosh "mongodb://root:root@localhost:27017/admin" scripts/persona_ddl_mongo.js
mongosh "mongodb://root:root@localhost:27017/admin" scripts/persona_dml_mongo.js
```

### 3. Configuración

Cada aplicación tiene su `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3307/persona_db
spring.datasource.username=persona_db
spring.datasource.password=persona_db
spring.mongodb.uri=mongodb://persona_db:persona_db@localhost:27017/persona_db?authSource=admin
```

Cualquier propiedad se puede sobreescribir con variables de entorno, por ejemplo
`SPRING_DATASOURCE_URL` o `SPRING_MONGODB_URI` (así lo hace el `docker-compose.yml`).

### 4. Compilar

```bash
mvn clean package -DskipTests
```

Genera los JAR ejecutables:

- `rest-input-adapter/target/rest-input-adapter-0.0.1-SNAPSHOT.jar`
- `cli-input-adapter/target/cli-input-adapter-0.0.1-SNAPSHOT.jar`

### 5. Ejecutar

```bash
java -jar rest-input-adapter/target/rest-input-adapter-0.0.1-SNAPSHOT.jar   # API en el puerto 3000
java -jar cli-input-adapter/target/cli-input-adapter-0.0.1-SNAPSHOT.jar     # menú de consola
```

En Windows, si la consola muestra mal las tildes: `chcp 65001` antes de ejecutar la CLI.

En un IDE (Eclipse / IntelliJ / VS Code) basta con importar el proyecto como proyecto Maven y ejecutar las clases
`PersonAppRestApi` o `PersonAppCli`. Lombok se procesa desde Maven; en Eclipse/STS además hay que instalar el
agente de Lombok.

## API REST

`{database}` es `MARIA` o `MONGO` (no distingue mayúsculas). En los `POST` la base va en el campo `database` del body,
como en el contrato original del template.

| Recurso | Método y ruta | Descripción |
|---|---|---|
| Persona | `GET /api/v1/persona/{database}` | Listar |
| | `GET /api/v1/persona/{database}/count` | Contar |
| | `GET /api/v1/persona/{database}/{cc}` | Buscar |
| | `GET /api/v1/persona/{database}/{cc}/telefonos` | Teléfonos de la persona |
| | `GET /api/v1/persona/{database}/{cc}/estudios` | Estudios de la persona |
| | `POST /api/v1/persona` | Crear |
| | `PUT /api/v1/persona/{database}/{cc}` | Editar |
| | `DELETE /api/v1/persona/{database}/{cc}` | Eliminar (en cascada teléfonos y estudios) |
| Profesión | `GET/POST/PUT/DELETE /api/v1/profesion/...` | Igual que persona, con `{id}` |
| Teléfono | `GET/POST/PUT/DELETE /api/v1/telefono/...` | Igual, con `{numero}`; el dueño debe existir |
| Estudio | `GET/PUT/DELETE /api/v1/estudio/{database}/{cc}/{idProfesion}` | Clave compuesta |
| | `POST /api/v1/estudio` | Crear; la persona y la profesión deben existir |

Ejemplos de body:

```json
POST /api/v1/persona
{ "dni": "1010", "firstName": "Ana", "lastName": "Gomez", "age": "25", "sex": "F", "database": "MARIA" }

POST /api/v1/profesion
{ "id": "50", "name": "Fisica", "description": "Ciencias basicas", "database": "MONGO" }

POST /api/v1/telefono
{ "number": "3000000001", "company": "Claro", "ownerId": "1010", "database": "MARIA" }

POST /api/v1/estudio
{ "personId": "1010", "professionId": "50", "graduationDate": "2020-05-10", "universityName": "Javeriana", "database": "MONGO" }
```

Códigos de respuesta: `200` OK, `201` creado, `400` datos o base inválidos, `404` no existe, `409` ya existe,
`500` error inesperado. Los errores devuelven `{ "status", "description", "date" }`.

Prueba de humo de todo el CRUD en ambas bases (requiere `curl`, con la API arriba):

```bash
bash scripts/smoke_test.sh
```

## Aplicación de consola

```
1 Personas   2 Profesiones   3 Teléfonos   4 Estudios   0 Salir
  └─ 1 MariaDB  2 MongoDB
       └─ 1 ver todo  2 buscar  3 crear  4 editar  5 eliminar  6 contar
          (personas además: 7 teléfonos de una persona, 8 estudios de una persona)
```

## Modelo de datos

```
persona(cc PK, nombre, apellido, genero ENUM('M','F'), edad)
profesion(id PK, nom, des)
telefono(num PK, oper, duenio FK -> persona.cc)
estudios(id_prof FK -> profesion.id, cc_per FK -> persona.cc, fecha, univer)  PK(id_prof, cc_per)
```

En MongoDB se usan las colecciones `persona`, `profesion`, `telefono` y `estudios`; las relaciones se guardan
como referencias por `_id` (`@DocumentReference`) y el `_id` de un estudio es `"cc-idProfesion"`.

## Autores

- Sebastián Fonseca
- Samuel
- Damián

Profesor: Andrés Martín Sánchez – Arquitectura de Software, Pontificia Universidad Javeriana.

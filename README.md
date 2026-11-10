# PersonApp – Arquitectura Hexagonal con Spring Boot

Servicio web REST y aplicación de consola (CLI) para el CRUD de **Persona, Profesión, Teléfono y Estudio**,
construido con **arquitectura hexagonal (puertos y adaptadores)**, los patrones **Repository** y **Service
(casos de uso)** y un proyecto **multimódulo Maven**. Cada operación se puede ejecutar sobre **MariaDB** o
**MongoDB**: la base se elige en cada petición.

> Laboratorio 2 – Arquitectura de Software – Pontificia Universidad Javeriana.
> Parte del template [andres-karoll/personapp-hexa-spring-boot](https://github.com/andres-karoll/personapp-hexa-spring-boot),
> que estaba incompleto y con errores. Lo que se encontró y cómo se corrigió está en
> [`docs/HALLAZGOS.md`](docs/HALLAZGOS.md).

## Contenido

1. [Tecnologías y versiones](#1-tecnologías-y-versiones)
2. [Requisitos](#2-requisitos)
3. [Obtener el código](#3-obtener-el-código)
4. [Opción A – Todo con Docker (recomendado)](#4-opción-a--todo-con-docker-recomendado)
5. [Opción B – Compilar y ejecutar desde la terminal](#5-opción-b--compilar-y-ejecutar-desde-la-terminal)
6. [Opción C – Ejecutar desde el IDE](#6-opción-c--ejecutar-desde-el-ide)
7. [Usar Swagger](#7-usar-swagger)
8. [Probar la API desde la terminal](#8-probar-la-api-desde-la-terminal)
9. [Usar la aplicación de consola (CLI)](#9-usar-la-aplicación-de-consola-cli)
10. [Configuración](#10-configuración)
11. [Bases de datos y scripts](#11-bases-de-datos-y-scripts)
12. [Estructura del proyecto](#12-estructura-del-proyecto)
13. [Problemas frecuentes](#13-problemas-frecuentes)

---

## 1. Tecnologías y versiones

| Herramienta | Versión |
|---|---|
| Java (JDK) | **25 LTS** (Eclipse Temurin) |
| Spring Boot | **4.1.2** (Spring Framework 7, Hibernate 7, Jakarta EE 11) |
| Maven | 3.9.x |
| MariaDB | **12.3 LTS** |
| MongoDB | **8.0** |
| springdoc-openapi (Swagger UI) | 3.2.0 – OpenAPI 3.1 |
| Lombok | 1.18.48 |
| Docker / Docker Compose | Docker Desktop reciente (Compose v2) |

## 2. Requisitos

| Si va a usar… | Necesita instalar |
|---|---|
| **Opción A (Docker)** | [Docker Desktop](https://www.docker.com/products/docker-desktop/) y [Git](https://git-scm.com/downloads). No necesita Java, Maven ni bases de datos. |
| **Opción B (terminal)** u **Opción C (IDE)** | Lo anterior + [JDK 25](https://adoptium.net/temurin/releases/?version=25) + [Maven 3.9+](https://maven.apache.org/download.cgi) (+ IntelliJ IDEA o VS Code para la opción C). Las bases de datos se levantan con Docker. |

En Windows, al instalar Docker Desktop deje marcada la opción **Use WSL 2**. Antes de usarlo, abra Docker Desktop
y espere a que abajo a la izquierda diga **Engine running**.

Todos los comandos de esta guía funcionan en **PowerShell**, **CMD** o **Git Bash** (Windows) y en la terminal de
macOS/Linux, salvo donde se indica.

## 3. Obtener el código

```bash
git clone https://github.com/sefonseca/personapp-hexa-spring-boot.git
cd personapp-hexa-spring-boot
git checkout v1.0.0
```

Todos los comandos siguientes se ejecutan **desde la carpeta `personapp-hexa-spring-boot`**.

---

## 4. Opción A – Todo con Docker (recomendado)

Levanta MariaDB, MongoDB y la API REST en contenedores; Docker compila el proyecto por dentro.

### 4.1 Levantar

```bash
docker compose up -d --build
```

La primera vez tarda entre 5 y 10 minutos (descarga imágenes y dependencias y compila). Las siguientes, segundos.
Al crear las bases por primera vez se ejecutan solos los scripts de `scripts/` con los datos iniciales.

### 4.2 Verificar

```bash
docker compose ps
```

Debe verse así:

```
NAME                IMAGE            STATUS                   PORTS
personapp-mariadb   mariadb:12.3     Up (healthy)             0.0.0.0:3307->3306/tcp
personapp-mongodb   mongo:8.0        Up (healthy)             0.0.0.0:27017->27017/tcp
personapp-rest      personapp-rest   Up                       0.0.0.0:3000->3000/tcp
```

La API tarda unos 20 segundos más en arrancar. Para ver cuándo está lista:

```bash
docker compose logs -f rest
```

Cuando aparezca `Started PersonAppRestApi`, salga del log con **Ctrl+C** (la API sigue corriendo) y abra
**<http://localhost:3000/swagger-ui.html>** (ver [sección 7](#7-usar-swagger)).

### 4.3 Aplicación de consola

```bash
docker compose run --rm cli
```

### 4.4 Comandos útiles

| Acción | Comando |
|---|---|
| Ver estado | `docker compose ps` |
| Ver el log de la API | `docker compose logs -f rest` |
| Detener solo la API (para correrla desde terminal o IDE) | `docker compose stop rest` |
| Volver a encender la API | `docker compose start rest` |
| Apagar todo y conservar los datos | `docker compose down` |
| Apagar todo y **volver a los datos iniciales** | `docker compose down -v` y luego `docker compose up -d --build` |
| Reconstruir tras cambiar el código | `docker compose up -d --build` |

| Servicio | Contenedor | Puerto en su equipo | Credenciales |
|---|---|---|---|
| MariaDB 12.3 | personapp-mariadb | 3307 | root / root · aplicación: persona_db / persona_db |
| MongoDB 8.0 | personapp-mongodb | 27017 | root / root · aplicación: persona_db / persona_db (`authSource=admin`) |
| API REST | personapp-rest | 3000 | – |

---

## 5. Opción B – Compilar y ejecutar desde la terminal

### 5.1 Instalar JDK 25 y Maven (Windows)

**JDK 25:** descargue el instalador `.msi` de [Temurin 25](https://adoptium.net/temurin/releases/?version=25)
y durante la instalación active **Set JAVA_HOME variable** y **Add to PATH**.

**Maven:**

1. En <https://maven.apache.org/download.cgi> descargue el **Binary zip archive** (`apache-maven-3.9.x-bin.zip`).
2. Cree la carpeta `C:\Program Files\Maven` (Explorador de archivos → `C:\Program Files` → clic derecho →
   Nuevo → Carpeta) y extraiga ahí el zip (clic derecho → Extraer todo…).
3. Agregue la carpeta `bin` al **PATH**: tecla Windows → escriba *variables de entorno* → **Editar las
   variables de entorno del sistema** → **Variables de entorno…** → en *Variables de usuario* seleccione
   **Path** → **Editar** → **Nuevo** → pegue la ruta, por ejemplo
   `C:\Program Files\Maven\apache-maven-3.9.11\bin` → **Aceptar** en todas las ventanas.
4. **Cierre todas las terminales** y abra una nueva.

Atajo en Windows 10/11 (PowerShell): `winget install EclipseAdoptium.Temurin.25.JDK` y `winget install Apache.Maven`.

En macOS: `brew install --cask temurin` y `brew install maven`. En Linux use el gestor de paquetes o SDKMAN.

Compruebe (debe decir **Java version: 25**):

```bash
java -version
mvn -version
```

### 5.2 Levantar solo las bases de datos

```bash
docker compose up -d mariadb mongodb
docker compose ps
```

Espere a que ambas digan **(healthy)**. Si la API de Docker estaba corriendo, deténgala para liberar el puerto
3000: `docker compose stop rest`.

> ¿Prefiere MariaDB y MongoDB instalados en su equipo, sin Docker? Vea la [sección 11.2](#112-crear-las-bases-sin-docker).

### 5.3 Compilar

```bash
mvn clean package -DskipTests
```

Debe terminar con `BUILD SUCCESS` en los 8 módulos y generar:

- `rest-input-adapter/target/rest-input-adapter-0.0.1-SNAPSHOT.jar`
- `cli-input-adapter/target/cli-input-adapter-0.0.1-SNAPSHOT.jar`

### 5.4 Ejecutar la API REST

```bash
java -jar rest-input-adapter/target/rest-input-adapter-0.0.1-SNAPSHOT.jar
```

Cuando aparezca `Started PersonAppRestApi`, abra **<http://localhost:3000/swagger-ui.html>**.
Deje esa terminal abierta; para detener la API presione **Ctrl+C**.

Para usar otro puerto: `java -jar rest-input-adapter/target/rest-input-adapter-0.0.1-SNAPSHOT.jar --server.port=3001`.

### 5.5 Ejecutar la aplicación de consola

En **otra** terminal, desde la carpeta del proyecto:

```bash
java -jar cli-input-adapter/target/cli-input-adapter-0.0.1-SNAPSHOT.jar
```

En la consola de Windows, si las tildes se ven mal, ejecute antes `chcp 65001`.

---

## 6. Opción C – Ejecutar desde el IDE

Requisitos: JDK 25 instalado y las bases levantadas (`docker compose up -d mariadb mongodb`). Si la API de
Docker está corriendo, deténgala con `docker compose stop rest`.

### IntelliJ IDEA (Community o Ultimate)

1. **File → Open…** → seleccione la carpeta del proyecto → **Trust Project**. Se importa como proyecto Maven;
   espere a que termine de indexar y descargar dependencias.
2. **File → Project Structure → Project → SDK:** elija el JDK 25 (si no aparece: *Add SDK → JDK…* y busque la
   carpeta de instalación de Temurin 25).
3. **Settings → Build, Execution, Deployment → Compiler → Annotation Processors →** marque **Enable annotation
   processing** (para Lombok).
4. API: abra `rest-input-adapter/src/main/java/co/edu/javeriana/as/personapp/PersonAppRestApi.java` →
   ▶ junto a la clase → **Run**.
5. CLI: abra `cli-input-adapter/src/main/java/co/edu/javeriana/as/personapp/PersonAppCli.java` → ▶ → **Run**.
   El menú aparece en la consola inferior; haga clic en ella para escribir.

### VS Code

1. Instale las extensiones **Extension Pack for Java** y **Spring Boot Extension Pack**.
2. **File → Open Folder…** → carpeta del proyecto; espere a que diga *Java: Ready*.
3. Abra `PersonAppRestApi.java` (o `PersonAppCli.java`) y haga clic en **Run** sobre el método `main`.

Para depurar, use **Debug** en lugar de **Run** y ponga un *breakpoint*, por ejemplo en
`application/src/main/java/co/edu/javeriana/as/personapp/application/usecase/PersonUseCase.java` (método `create`).

---

## 7. Usar Swagger

Con la API corriendo (opción A, B o C):

| Recurso | URL |
|---|---|
| Swagger UI | <http://localhost:3000/swagger-ui.html> (redirige a `/swagger-ui/index.html`) |
| Documento OpenAPI (JSON) | <http://localhost:3000/api-docs> |

Cómo ejecutar una operación:

1. Abra el grupo (**Persona**, **Profesion**, **Telefono** o **Estudio**) y la operación, por ejemplo
   `GET /api/v1/persona/{database}`.
2. Clic en **Try it out**.
3. Llene los parámetros: en `database` escriba **`MARIA`** o **`MONGO`**. En los `POST`/`PUT` pegue el JSON en
   *Request body*.
4. Clic en **Execute**. Abajo verá el *Response code* y el *Response body*.

Recorrido de prueba sugerido:

| # | Operación | Datos | Resultado esperado |
|---|---|---|---|
| 1 | `GET /api/v1/persona/{database}` | `MARIA`, luego `MONGO` | 200, las 5 personas iniciales en cada base |
| 2 | `GET /api/v1/persona/{database}/{id}/telefonos` | `MARIA`, `123456789` | 200, 2 teléfonos de Pepe Perez |
| 3 | `POST /api/v1/persona` | body de persona (abajo) | **201**; si lo repite, **409** (ya existe) |
| 4 | `POST /api/v1/telefono` | body de teléfono con `ownerId` `1010` | **201**; con `ownerId` `777`, **404** (el dueño no existe) |
| 5 | `POST /api/v1/estudio` | body de estudio | **201**; con fecha `2099-01-01`, **400** (fecha futura) |
| 6 | `PUT /api/v1/persona/{database}/{id}` | `MARIA`, `1010`, body sin `dni` ni `database` | 200 con los datos nuevos |
| 7 | `DELETE /api/v1/persona/{database}/{id}` | `MARIA`, `1010` | 200; sus teléfonos y estudios se borran en cascada |
| 8 | `GET /api/v1/persona/{database}` | `OTRA` | **400** (base inválida) |

Bodies de ejemplo (en los `POST` la base va en el campo `database`; en `PUT` va en la URL):

```json
{ "dni": "1010", "firstName": "Ana", "lastName": "Gomez", "age": "25", "sex": "F", "database": "MARIA" }
```
```json
{ "id": "50", "name": "Fisica", "description": "Ciencias basicas", "database": "MARIA" }
```
```json
{ "number": "3000000001", "company": "Claro", "ownerId": "1010", "database": "MARIA" }
```
```json
{ "personId": "1010", "professionId": "1", "graduationDate": "2020-05-10", "universityName": "Javeriana", "database": "MARIA" }
```

Reglas de los campos: `sex` acepta `M` o `F`; `age` es opcional; `graduationDate` usa el formato `yyyy-MM-dd`.

### Endpoints

`{database}` = `MARIA` o `MONGO` (sin distinguir mayúsculas).

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
| Profesión | `GET /api/v1/profesion/{database}` · `/count` · `/{id}` | Listar · contar · buscar |
| | `POST /api/v1/profesion` · `PUT` / `DELETE /api/v1/profesion/{database}/{id}` | Crear · editar · eliminar (en cascada sus estudios) |
| Teléfono | `GET /api/v1/telefono/{database}` · `/count` · `/{numero}` | Listar · contar · buscar |
| | `POST /api/v1/telefono` · `PUT` / `DELETE /api/v1/telefono/{database}/{numero}` | Crear (el dueño debe existir) · editar · eliminar |
| Estudio | `GET /api/v1/estudio/{database}` · `/count` · `/{cc}/{idProfesion}` | Listar · contar · buscar |
| | `POST /api/v1/estudio` · `PUT` / `DELETE /api/v1/estudio/{database}/{cc}/{idProfesion}` | Crear (persona y profesión deben existir) · editar · eliminar |

Códigos de respuesta: `200` OK · `201` creado · `400` datos o base inválidos · `404` no existe · `409` ya existe ·
`500` error inesperado. Los errores devuelven `{ "status", "description", "date" }`.

---

## 8. Probar la API desde la terminal

### Git Bash, macOS o Linux (`curl`)

```bash
# Listar personas de MariaDB y buscar una en MongoDB
curl http://localhost:3000/api/v1/persona/MARIA
curl http://localhost:3000/api/v1/persona/MONGO/123456789

# Crear, editar, consultar y eliminar
curl -X POST http://localhost:3000/api/v1/persona -H "Content-Type: application/json" \
  -d '{"dni":"1010","firstName":"Ana","lastName":"Gomez","age":"25","sex":"F","database":"MARIA"}'
curl -X PUT http://localhost:3000/api/v1/persona/MARIA/1010 -H "Content-Type: application/json" \
  -d '{"firstName":"Ana Maria","lastName":"Gomez","age":"26","sex":"F"}'
curl -X POST http://localhost:3000/api/v1/telefono -H "Content-Type: application/json" \
  -d '{"number":"3000000001","company":"Claro","ownerId":"1010","database":"MARIA"}'
curl http://localhost:3000/api/v1/persona/MARIA/1010/telefonos
curl -X DELETE http://localhost:3000/api/v1/persona/MARIA/1010

# Ver el código HTTP de la respuesta
curl -i http://localhost:3000/api/v1/telefono/MARIA/3000000001
```

### PowerShell (Windows)

En PowerShell `curl` no es el `curl` real; use `Invoke-RestMethod`:

```powershell
$api = "http://localhost:3000/api/v1"

# Listar personas en forma de tabla
Invoke-RestMethod "$api/persona/MARIA" | Format-Table dni, firstName, lastName, age, sex

# Crear una persona en MongoDB
$persona = @{ dni="1010"; firstName="Ana"; lastName="Gomez"; age="25"; sex="F"; database="MONGO" } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri "$api/persona" -ContentType "application/json" -Body $persona

# Editar y eliminar
$cambios = @{ firstName="Ana Maria"; lastName="Gomez"; age="26"; sex="F" } | ConvertTo-Json
Invoke-RestMethod -Method Put -Uri "$api/persona/MONGO/1010" -ContentType "application/json" -Body $cambios
Invoke-RestMethod -Method Delete -Uri "$api/persona/MONGO/1010"
```

Cuando la API responde un error (404, 409, 400), PowerShell lo muestra en rojo con el JSON del error; es el
comportamiento esperado.

### Prueba completa automática

Desde **Git Bash**, macOS o Linux (con la API corriendo):

```bash
bash scripts/smoke_test.sh
```

Ejecuta 68 peticiones (el CRUD de las 4 entidades en MARIA y MONGO, incluidos los casos de error) y muestra el
código de cada una. Ninguna debe empezar por `[5`. Al final deja los datos como estaban.

---

## 9. Usar la aplicación de consola (CLI)

Se inicia con `docker compose run --rm cli` (opción A), `java -jar cli-input-adapter/target/cli-input-adapter-0.0.1-SNAPSHOT.jar`
(opción B) o desde el IDE (opción C). Se navega escribiendo el número de la opción y Enter:

```
1 Personas   2 Profesiones   3 Teléfonos   4 Estudios   0 Salir
  └─ 1 MariaDB   2 MongoDB   0 Regresar
       └─ 1 Ver todo   2 Buscar   3 Crear   4 Editar   5 Eliminar   6 Contar   0 Regresar
          (en Personas además: 7 Teléfonos de una persona   8 Estudios de una persona)
```

Ejemplo: `1` → `1` → `1` lista las personas de MariaDB; `0` → `0` → `0` sale.

---

## 10. Configuración

Cada aplicación tiene su archivo `src/main/resources/application.properties`
(`rest-input-adapter` y `cli-input-adapter`; `server.port` y `springdoc` solo existen en la API REST):

```properties
server.port=3000
spring.datasource.url=jdbc:mariadb://localhost:3307/persona_db
spring.datasource.username=persona_db
spring.datasource.password=persona_db
spring.mongodb.uri=mongodb://persona_db:persona_db@localhost:27017/persona_db?authSource=admin
springdoc.api-docs.path=/api-docs
```

Cualquier propiedad se puede cambiar sin tocar el archivo:

- con variables de entorno, por ejemplo `SPRING_DATASOURCE_URL` o `SPRING_MONGODB_URI` (así lo hace
  `docker-compose.yml` para apuntar a los contenedores `mariadb` y `mongodb`);
- con argumentos al ejecutar el JAR, por ejemplo `--server.port=3001`.

Nota: en Spring Boot 4 la conexión a MongoDB se configura con `spring.mongodb.*`; las propiedades antiguas
`spring.data.mongodb.*` se ignoran.

---

## 11. Bases de datos y scripts

### 11.1 Scripts

| Archivo | Contenido |
|---|---|
| `scripts/persona_ddl_maria.sql` | Crea la base `persona_db`, el usuario `persona_db` y las tablas |
| `scripts/persona_dml_maria.sql` | Datos iniciales: 5 personas, 4 profesiones, 4 teléfonos, 4 estudios |
| `scripts/persona_ddl_mongo.js` | Crea el usuario `persona_db` (en la base `admin`) con permisos sobre `persona_db` |
| `scripts/persona_dml_mongo.js` | Los mismos datos iniciales en las colecciones de MongoDB |
| `scripts/smoke_test.sh` | Prueba automática de la API |

Con Docker se ejecutan solos la primera vez (`docker-entrypoint-initdb.d`).

### 11.2 Crear las bases sin Docker

Con MariaDB escuchando en el puerto **3307** y MongoDB en el **27017**:

```bash
mariadb -uroot -p -P3307 -h127.0.0.1 < scripts/persona_ddl_maria.sql
mariadb -uroot -p -P3307 -h127.0.0.1 < scripts/persona_dml_maria.sql
mongosh "mongodb://root:root@localhost:27017/admin" scripts/persona_ddl_mongo.js
mongosh "mongodb://root:root@localhost:27017/admin" scripts/persona_dml_mongo.js
```

(Cambie `root:root` por el usuario administrador de su MongoDB. En PowerShell la redirección `<` no existe:
use `Get-Content scripts/persona_ddl_maria.sql | mariadb -uroot -p -P3307 -h127.0.0.1`).

### 11.3 Modelo de datos

```
persona(cc PK, nombre, apellido, genero ENUM('M','F'), edad)
profesion(id PK, nom, des)
telefono(num PK, oper, duenio FK -> persona.cc)
estudios(id_prof FK -> profesion.id, cc_per FK -> persona.cc, fecha, univer)   PK(id_prof, cc_per)
```

En MongoDB se usan las colecciones `persona`, `profesion`, `telefono` y `estudios`; las relaciones se guardan
como el `_id` del documento referido (`@DocumentReference`) y el `_id` de un estudio es `"cc-idProfesion"`.

---

## 12. Estructura del proyecto

```
personapp-hexa-spring-boot (pom padre)
├── common                 Anotaciones (@Adapter, @UseCase, @Port, @Mapper), excepciones y DatabaseOption
├── domain                 Modelo de dominio puro: Person, Profession, Phone, Study, Gender
├── application            Puertos de entrada (in), puertos de salida (out) y casos de uso (servicios)
├── maria-output-adapter   Adaptador de salida JPA/MariaDB: entidades, repositorios, mappers, adaptadores
├── mongo-output-adapter   Adaptador de salida MongoDB: documentos, repositorios, mappers, adaptadores
├── rest-input-adapter     Adaptador de entrada REST + Swagger          -> aplicación ejecutable (puerto 3000)
├── cli-input-adapter      Adaptador de entrada de consola              -> aplicación ejecutable
├── scripts                DDL, DML y prueba automática
├── docs/HALLAZGOS.md      Errores encontrados en el template y su corrección
├── Dockerfile             Compilación multietapa (Maven + JDK 25 → JRE 25) de REST y CLI
└── docker-compose.yml     MariaDB + MongoDB + API REST + CLI
```

Dependencias entre módulos (la flecha indica "depende de"):

```
rest-input-adapter ─┐                 ┌─> maria-output-adapter ─┐
                    ├─> (ambos) ──────┤                         ├─> application ─> domain ─> common
cli-input-adapter ──┘                 └─> mongo-output-adapter ─┘
```

El dominio y la aplicación no conocen JPA, MongoDB ni HTTP: solo conocen los puertos. Los adaptadores de entrada
reciben los dos adaptadores de salida y, en cada petición, crean el caso de uso con el de la base pedida.

---

## 13. Problemas frecuentes

| Síntoma | Causa | Solución |
|---|---|---|
| `Port 3000 was already in use` al correr el JAR o el IDE | La API de Docker (`personapp-rest`) u otra ejecución ya usa el puerto | `docker compose stop rest`, o cierre la otra ejecución, o use `--server.port=3001` |
| `port is already allocated` (3307 o 27017) en `docker compose up` | Tiene MariaDB/MySQL o MongoDB instalados y corriendo en su equipo | Deténgalos (Servicios de Windows) o cambie el puerto izquierdo en `docker-compose.yml` |
| `Cannot connect to the Docker daemon` / `error during connect` | Docker Desktop cerrado o iniciando | Abra Docker Desktop y espere *Engine running* |
| La API se reinicia o falla al conectar con la base | Las bases aún no estaban listas | Espere a que estén *healthy*; revise `docker compose logs rest` |
| No aparecen los datos iniciales | Quedó un volumen de una ejecución anterior | `docker compose down -v` y `docker compose up -d --build` |
| `mvn no se reconoce como un comando` | Maven no está en el PATH o la terminal es anterior al cambio | Revise la [sección 5.1](#51-instalar-jdk-25-y-maven-windows) y abra una terminal nueva |
| `release version 25 not supported` / `mvn -version` muestra otro Java | `JAVA_HOME` apunta a otro JDK | Haga que `JAVA_HOME` apunte a la carpeta del JDK 25 |
| Errores de `getX()`/`setX()` en el IDE | Lombok no se está procesando | Active *Annotation Processing* (IntelliJ) o limpie el workspace de Java (VS Code) |
| Tildes raras en la CLI (Windows) | Página de códigos de la consola | `chcp 65001` antes de ejecutar |
| `400` con "Invalid database option" | `database` distinto de `MARIA`/`MONGO` | Use `MARIA` o `MONGO` |

---

## Autores

- Sebastián Fonseca
- Samuel
- Damián

Profesor: Andrés Martín Sánchez – Arquitectura de Software, Pontificia Universidad Javeriana.

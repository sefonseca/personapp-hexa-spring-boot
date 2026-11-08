# Hallazgos sobre el template original

Análisis del repositorio base `andres-karoll/personapp-hexa-spring-boot` (último commit del template: `b9bc9c8`).
Las líneas indicadas son las del **código original**. La regla fue hacerlo funcionar **sin cambiar la
estructura**: se mantuvieron los módulos, paquetes, clases, nombres de métodos y el contrato REST existente
(`GET /api/v1/persona/{database}` y `POST /api/v1/persona` con `database` en el body), y se completó lo que faltaba
siguiendo los mismos patrones del template.

Rutas abreviadas: `.../` = `src/main/java/co/edu/javeriana/as/personapp/`.

## 1. Errores que impedían que funcionara

| # | Archivo (original) | Línea | Problema | Corrección |
|---|---|---|---|---|
| 1 | `rest-input-adapter/.../mapper/PersonaMapperRest.java` | 31 | `fromAdapterToDomain` era un stub (`// TODO`) que hacía `return new Person();`. El `POST /persona` mandaba una persona vacía al caso de uso, que fallaba con `NullPointerException` al mapear el género (`@NonNull`) en el adaptador de salida. | Se implementó la conversión campo por campo con validación (`RestValidator`): `dni` y `age` numéricos, `sex` M/F. |
| 2 | `maria-output-adapter/.../mariadb/mapper/EstudiosMapperMaria.java` | 50 | `fromAdapterToDomain` armaba el `Study` y luego hacía `return null;`. | `return study;` |
| 3 | `mongo-output-adapter/.../mongo/mapper/EstudiosMapperMongo.java` | 61 | Mismo error: `return null;`. | `return study;` |
| 4 | `maria-output-adapter/.../mariadb/mapper/EstudiosMapperMaria.java` | 54 | `fecha.toInstant()`: Hibernate entrega un `java.sql.Date` para `@Temporal(DATE)` y `java.sql.Date.toInstant()` lanza `UnsupportedOperationException`. Estaba oculto por el error 2: en cuanto un estudio se mapeaba, toda consulta de personas o profesiones en MariaDB respondía 500. | Si es `java.sql.Date` se usa `toLocalDate()`. |
| 5 | `PersonaMapperMaria.java` 82/88, `TelefonoMapperMaria.java` 27/39, `EstudiosMapperMaria.java` 46-47, `ProfesionMapperMaria.java`, y sus equivalentes Mongo (`TelefonoMapperMongo.java` 27/39, `EstudiosMapperMongo.java` 57-58) | – | **Recursión infinita entre mappers**: Persona → sus teléfonos → el dueño (Persona) → sus teléfonos → … Lo mismo con estudios y profesiones. Con cualquier persona que tuviera teléfonos o estudios se producía `StackOverflowError`. | Se agregaron `fromDomainToAdapterBasic` / `fromAdapterToDomainBasic` (solo atributos simples, sin listas) en los mappers de Persona y Profesión; Teléfono y Estudio usan esas versiones para la referencia al dueño, la persona y la profesión. |
| 6 | `domain/.../domain/Person.java` | 32 | `isValidAge()` hacía `this.age >= 0`; con edad `null` (permitido por el modelo, ej. *Pepito Perez*) lanzaba `NullPointerException` por el unboxing. | `return this.age == null \|\| this.age >= 0;` |
| 7 | `PersonaMapperMaria.java` | 41 | `Gender.OTHER` se mapeaba a `' '`, pero la columna es `ENUM('M','F')` (`persona_ddl_maria.sql` línea 18): MariaDB rechaza el valor. | El mapper lanza `IllegalArgumentException` (400) para OTHER y las entradas REST/CLI solo aceptan M o F, respetando el modelo del enunciado. En Mongo se guarda `"O"` en lugar de `" "`. |
| 8 | `scripts/persona_dml_mongo.js` | 1 | `use prueba_db`: los datos se insertaban en una base distinta a la que usa la app (`persona_db`). | `db = db.getSiblingDB("persona_db")`. |
| 9 | `scripts/persona_ddl_mongo.js` / `persona_dml_mongo.js` | 1 | `use admin` / `use ...` son comandos interactivos de la shell; al ejecutar los scripts como archivo (por ejemplo en `docker-entrypoint-initdb.d`) no son confiables. | Se usa `db.getSiblingDB(...)` y el DDL elimina el usuario si ya existe para poder re-ejecutarse. |
| 10 | `cli-input-adapter/.../terminal/menu/MenuPrincipal.java` 79-82 y `PersonaMenu.java` 88-91 | – | `leerOpcion()` usaba `nextInt()` y, ante una entrada no numérica, se llamaba recursivamente **sin consumir el token inválido**: ciclo infinito hasta `StackOverflowError`. | Nueva clase `LectorConsola` que lee la línea completa y la convierte; ante error pide de nuevo. |
| 11 | `rest-input-adapter/.../controller/PersonaControllerV1.java` | 31 | `@PathVariable String database` sin nombre explícito. Desde Spring 6.1 los nombres de parámetros solo se conocen si se compila con `-parameters` (lo configuraba `spring-boot-starter-parent`, que este proyecto **no** usa). | `@PathVariable("database")` en todos los controladores y `<parameters>true</parameters>` en el `maven-compiler-plugin`. |

## 2. Errores de diseño / malas prácticas

| # | Archivo (original) | Línea | Problema | Corrección |
|---|---|---|---|---|
| 12 | `rest-input-adapter/.../adapter/PersonaInputAdapterRest.java` | 38, 42, 45 | El caso de uso se guardaba en un **atributo** (`personInputPort`) de un bean **singleton**. Dos peticiones simultáneas, una a MARIA y otra a MONGO, se pisaban entre sí (condición de carrera). | `setPersonOutputPortInjection` ahora **retorna** el caso de uso y se usa en una variable local por petición. (En la CLI se mantuvo el atributo porque es de un solo hilo.) |
| 13 | `PersonaInputAdapterRest.java` | 65, 73, 78 | Errores silenciados: con base inválida devolvía lista vacía con 200; `crearPersona` devolvía `null` (200 con cuerpo vacío); además siempre respondía `"MariaDB"` aunque se creara en Mongo. | Las excepciones suben al controlador y un `@RestControllerAdvice` (`GlobalExceptionHandler`) las traduce: `NoExistException` → 404, `AlreadyExistException` → 409, `InvalidOptionException` / `IllegalArgumentException` → 400, otras → 500. Se responde con la base real. |
| 14 | `rest-input-adapter/.../mapper/PersonaMapperRest.java` | 23 | `person.getAge()+""` producía el texto `"null"` cuando no hay edad. | Se devuelve `null`. |
| 15 | `application/.../usecase/PersonUseCase.java` | 33-36 | `create` guardaba sin verificar si la persona existía: un `POST` repetido **sobrescribía** silenciosamente (JPA `merge` / Mongo `save`). | Nueva excepción `AlreadyExistException` en `common`; `create` la lanza si el id ya existe. `edit` fuerza el id de la URL. |
| 16 | `cli-input-adapter/.../terminal/menu/MenuPrincipal.java` | 13 | Anotada con `@Controller`, estereotipo de Spring MVC, en una aplicación de consola. | `@Component`. |
| 17 | `rest-input-adapter/src/main/resources/application.properties` | 8 | `spring.main.allow-circular-references=true` oculta que los mappers se inyectan en ciclo (`PersonaMapper` ↔ `EstudiosMapper` / `TelefonoMapper`). | Se **mantiene** para no reestructurar los mappers del template, pero queda documentado. La forma limpia sería que los mappers básicos no dependan entre sí (inyección por constructor sin ciclos). |
| 18 | `cli-input-adapter/.../terminal/adapter/PersonaInputAdapterCli.java` | – | `historial()` y `historial1()` hacen lo mismo (código duplicado). | Se dejaron ambos para no borrar código del template; el menú usa `historial()`. |
| 19 | `MongoDB` (adaptadores) | – | Mongo no tiene integridad referencial: al borrar una persona o profesión quedaban teléfonos/estudios huérfanos, a diferencia de MariaDB (`CascadeType.ALL`). | `PersonOutputAdapterMongo` y `ProfessionOutputAdapterMongo` eliminan en cascada con `MongoTemplate`. |
| 20 | `scripts/persona_dml_mongo.js` | – | Las fechas guardadas a medianoche UTC se convierten a `LocalDate` en la zona local; en Colombia (UTC-5) se mostraba el día anterior. | Las fechas del script se guardan a mediodía UTC. |

## 3. Funcionalidad que faltaba (el template estaba incompleto)

| Capa | Antes | Ahora |
|---|---|---|
| `application` (puertos y casos de uso) | Solo Persona | `ProfessionInputPort/OutputPort/UseCase`, `PhoneInputPort/OutputPort/UseCase`, `StudyInputPort/OutputPort/UseCase`. Reglas: no duplicados, el dueño de un teléfono debe existir, la persona y la profesión de un estudio deben existir, la fecha de grado no puede ser futura. |
| `maria-output-adapter` | Solo `PersonaRepositoryMaria` y `PersonOutputAdapterMaria` | Repositorios y adaptadores para profesión, teléfono y estudios (`EstudiosEntityPK` como clave compuesta). |
| `mongo-output-adapter` | Solo persona | Repositorios y adaptadores para profesión, teléfono y estudios. |
| `rest-input-adapter` | Solo listar y crear (crear no funcionaba) | CRUD completo de las 4 entidades + contar + teléfonos/estudios de una persona, códigos HTTP correctos y Swagger documentado. |
| `cli-input-adapter` | Solo "ver todas las personas"; módulos 2-4 solo hacían `log.warn("Implementar Menu")` (`MenuPrincipal.java` 51, 54, 57) | Menús de CRUD completos para las 4 entidades en ambas bases. |
| `scripts` | DML solo de personas | DML de profesiones, teléfonos y estudios para MariaDB y MongoDB. |
| Despliegue | Sin Docker | `Dockerfile` multi-etapa + `docker-compose.yml` (MariaDB, MongoDB, REST y CLI). |

## 4. Actualización de versiones (requerida por el laboratorio)

| Elemento | Antes | Ahora | Cambios que obligó |
|---|---|---|---|
| Java | 11 | 25 LTS | `maven.compiler.release`; Lombok declarado en `annotationProcessorPaths` (desde JDK 23 los procesadores no se descubren solos). |
| Spring Boot | 2.7.11 (sin soporte) | 4.1.2 | `javax.persistence` → `jakarta.persistence`, `javax.transaction` → `jakarta.transaction`; `spring-boot-starter-web` → `spring-boot-starter-webmvc`; propiedades `spring.data.mongodb.*` → `spring.mongodb.*`; versiones gestionadas con el BOM `spring-boot-dependencies` en lugar de fijarlas a mano en cada módulo. |
| springdoc | `springdoc-openapi-ui` 1.7.0 | `springdoc-openapi-starter-webmvc-ui` 3.2.0 | El artefacto cambió de nombre para Boot 3+. |
| Lombok | 1.18.26 | 1.18.48 | Soporte de JDK 25. |
| `ProfesionEntity.des` | `@Lob` | `@Column(columnDefinition = "TEXT")` | La columna es `TEXT`; con `@Lob` Hibernate 7 la trata como CLOB. |
| CLI | dependencias JAXB | eliminadas | No se usaban (restos de JDK 8). |
| `log4j-to-slf4j` | declarado en el pom padre | eliminado | Ya lo trae el starter de logging de Spring Boot. |

## 5. README original

- Indicaba Swagger en `/swagger-ui.html` (sigue funcionando, redirige a `/swagger-ui/index.html`).
- No explicaba cómo crear las bases, el usuario de Mongo en `admin` (`authSource=admin`) ni cómo compilar.
  Todo eso quedó en el `README.md`.

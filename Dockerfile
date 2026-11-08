# ---------- Etapa 1: compilacion de todos los modulos con Maven + JDK 25 ----------
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /build
COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn -B -DskipTests package

# ---------- Etapa 2a: imagen de la API REST ----------
FROM eclipse-temurin:25-jre AS rest
WORKDIR /app
COPY --from=build /build/rest-input-adapter/target/rest-input-adapter-*.jar app.jar
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "app.jar"]

# ---------- Etapa 2b: imagen de la aplicacion de consola ----------
FROM eclipse-temurin:25-jre AS cli
WORKDIR /app
COPY --from=build /build/cli-input-adapter/target/cli-input-adapter-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

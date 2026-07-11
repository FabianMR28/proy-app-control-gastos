FROM gradle:8.14-jdk21 AS build

WORKDIR /app

# Copiar archivos de configuración primero para aprovechar la caché
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Descargar dependencias
RUN gradle dependencies --no-daemon

# Copiar el código fuente
COPY src ./src

# Generar el JAR
RUN gradle bootJar --no-daemon

# Etapa de ejecución
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
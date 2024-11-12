# Usa la imagen base de OpenJDK para construcción y empaquetado
FROM openjdk:23-jdk-slim AS build

# Establece el directorio de trabajo
WORKDIR /app

# Copia los archivos de tu proyecto al contenedor
COPY . .

# Construye el proyecto con Maven (puedes cambiarlo si usas Gradle)
RUN ./mvnw clean package -DskipTests

# Crea la imagen final solo con el .jar
FROM openjdk:23-jdk-slim

# Copia el .jar desde la etapa de construcción
COPY --from=build /app/target/S5T1-0.0.1-SNAPSHOT.jar /app/app.jar

# Expone el puerto en el que corre tu aplicación
EXPOSE 8080

server.address=0.0.0.0

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

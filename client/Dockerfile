FROM openjdk:17-jdk-slim

# Instalar herramientas necesarias
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Crear directorio de trabajo
WORKDIR /app

# Copiar el JAR construido
COPY build/libs/client.jar app.jar

# Exponer puerto
EXPOSE 8081

# Comando para ejecutar la aplicación con perfil Docker
CMD ["java", "-jar", "-Dspring.profiles.active=docker", "app.jar"]

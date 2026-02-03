# PHASE 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# PHASE 2: Run the application
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render assigns a dynamic port
EXPOSE 6789

# Run the app with dynamic port support
ENTRYPOINT ["java", "-Dserver.port=${PORT:-6789}", "-jar", "app.jar"]
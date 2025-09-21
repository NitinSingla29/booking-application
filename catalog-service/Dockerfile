# ===== Stage: Runtime =====
FROM openjdk:23-jdk-slim

WORKDIR /app

# Copy pre-built jar from local machine
COPY target/catalog-service-0.0.1-SNAPSHOT.jar catalog-service.jar

# Expose Spring Boot port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java","-jar","catalog-service.jar"]

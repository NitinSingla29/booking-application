# ===== Stage: Runtime =====
FROM openjdk:23-jdk-slim

WORKDIR /app

# Copy pre-built jar from local machine
COPY target/booking-service-0.0.1-SNAPSHOT.jar booking-service.jar

# Expose Spring Boot port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java","-jar","booking-service.jar"]

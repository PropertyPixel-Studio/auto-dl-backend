# Stage 1: Build the application
FROM maven:3.9.4-eclipse-temurin-21-alpine AS build

# Set the working directory
WORKDIR /app

# Copy only the POM file first to leverage Docker cache for dependencies
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the project, packaging it as a JAR
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine

# Create a non-root user to run the application
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar /app/app.jar

# Set the server port as an environment variable
ENV SERVER_PORT=50021

# Expose the specified port
EXPOSE 50021

# Run the application with optimized JVM settings
CMD ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar", "--server.port=${SERVER_PORT}"]

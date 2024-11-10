# Stage 1: Build the application
FROM maven:3.9.4-eclipse-temurin-21 AS build

# Set the working directory
WORKDIR /app

# Copy the project files to the container
COPY . .

# Build the project, packaging it as a JAR
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-jammy

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar /app/app.jar

# Set the server port as an environment variable
ENV SERVER_PORT=3333

# Expose the specified port
EXPOSE 3333

# Run the application on the specified port
CMD ["java", "-jar", "/app/app.jar", "--server.port=${SERVER_PORT}"]

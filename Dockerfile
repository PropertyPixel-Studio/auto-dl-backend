# Use a base image with Java and Maven
FROM maven:3.8.6-openjdk-17 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the source code to the container
COPY . .

# Build the application
RUN mvn clean package -DskipTests

# Use a lighter image for running the application
FROM openjdk:17-jdk-slim

# Copy the built .jar file from the build stage
COPY --from=build /app/target/*.jar /app/app.jar

# Expose the application port
EXPOSE 3333

# Run the application
CMD ["java", "-jar", "/app/app.jar"]

# Use an official Maven image to build the project
FROM maven:3.8.6-openjdk-11 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the project files to the container
COPY . .

# Build the project and create an executable jar
RUN mvn clean package

# Use an official OpenJDK image for the runtime
FROM openjdk:11-jdk-slim

# Set the working directory for the runtime
WORKDIR /app

# Copy the built jar from the previous stage
COPY --from=build /app/target/MedHotelV2-1.0-SNAPSHOT.jar /app/MedHotelV2.jar

# Command to run the application
CMD ["java", "-jar", "/app/MedHotelV2.jar"]

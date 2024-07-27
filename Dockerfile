# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jre-slim

# Set the working directory in the container
WORKDIR /app

# Copy the executable jar file to the container
COPY target/eventsProject-1.0.0.jar /app/eventsProject-1.0.0.jar

# Run the jar file
ENTRYPOINT ["java", "-jar", "/app/eventsProject-1.0.0.jar"]

EXPOSE 8089
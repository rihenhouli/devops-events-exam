# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jre-slim

# Set the working directory in the container
WORKDIR /app

# Copy the executable jar file to the container
COPY target/eventsProject-1.jar /app/eventsProject-1.jar

# Run the jar file
ENTRYPOINT ["java", "-jar", "/app/eventsProject-1.jar"]

EXPOSE 8089
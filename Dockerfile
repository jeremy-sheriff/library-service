FROM openjdk:17-jdk-slim

# Install curl
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Set the working directory to /app
WORKDIR /app

EXPOSE 8080

COPY build/libs/library-0.0.1-SNAPSHOT.jar /app/library.jar

# Run the application
CMD ["java", "-jar", "library.jar"]

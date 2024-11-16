FROM gradle:7.5-jdk17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy build.gradle.kts and settings.gradle.kts to cache dependencies
COPY build.gradle.kts settings.gradle.kts /app/

# Download dependencies
RUN gradle dependencies --no-daemon || true

# Copy the rest of the source code
COPY . /app

# Build the application
RUN gradle build --no-daemon -x test


# Stage 2: Runtime
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/build/libs/library-0.0.1-SNAPSHOT.jar /app/library.jar

# Expose the port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "library.jar"]


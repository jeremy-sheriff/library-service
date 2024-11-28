# Library Service Application with Kotlin and Gradle

A simple Spring Boot application built with Kotlin and Gradle. This project demonstrates [brief description of the project, e.g., managing users, inventory system, or any specific use case].

## Table of Contents

- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [Testing](#testing)
- [Configuration](#configuration)
- [Technologies Used](#technologies-used)
- [License](#license)

## Env Variables
 ``` bash
    DOCKER_IMAGE_NAME=your_image_name
    STUDENTS_URL=http://students:8080/api
    KEY_CLOAK_USERNAME=${KEY_CLOAK_USERNAME}
    KEY_CLOAK_CLIENT_ID=${KEY_CLOAK_CLIENT_ID}
    KEY_CLOAK_PASSWORD=${KC_DB_PASSWORD}
    KEY_CLOAK_ISSUER_URI=http://keycloak:8080/keycloak/auth/realms/school
    
    DB_PASSWORD=${DB_PASSWORD}
    DB_USERNAME=${DB_USERNAME}
    DB_URL=${DB_URL}
    
    CORS_ALLOWED_ORIGINS=http://localhost
    
    AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}
    AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}
    AWS_JAVA_V1_DISABLE_DEPRECATION_ANNOUNCEMENT=true
    AWS_SQS_URL=${AWS_SQS_URL}

    SPRING_PROFILES_ACTIVE=prod
 
 ```
## Prerequisites

Before running the application, ensure you have the following installed:

- **Java 17** (or later) - Make sure you have JDK 17 or above installed.
- **Gradle** - Build tool for dependency management and packaging.
- **Git** - For cloning the repository.

To verify if you have Java and Gradle installed, you can run the following commands:

```bash
java -version
gradle -v

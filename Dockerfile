# Stage 1: Build the application
# Use a Maven image to build the application
FROM maven:3.9.9-amazoncorretto-23 AS build

# Copy source code and pom.xml file to /app folder
WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Create the final image
# Use Amazon Corretto 23 as the base image
FROM amazoncorretto:23.0.1-alpine3.20

RUN apk update && \
    apk add --no-cache fontconfig ttf-dejavu

# Set the working directory
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Use the official Gradle image to create a build artifact.
# https://hub.docker.com/_/gradle
FROM gradle:8.5.0-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

# Run the application on official OpenJDK base image.
FROM openjdk:17-oracle
EXPOSE 8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/bustravel-1.jar /app/bustravel.jar
ENTRYPOINT ["java", "-jar", "/app/bustravel.jar"]
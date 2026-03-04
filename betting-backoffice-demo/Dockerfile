# syntax=docker/dockerfile:1.5

# Fase di build (multi-arch)
FROM maven:3.9.6-eclipse-temurin-8 AS build
WORKDIR /home/app
COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -B -DskipTests package

# Fase di runtime (multi-arch, NON alpine)
# runtime (prima avevi 8-jre)
FROM eclipse-temurin:8-jdk
WORKDIR /app
COPY --from=build /home/app/target/*.jar /app/app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]


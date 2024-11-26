# Development stage
FROM eclipse-temurin:21-jdk-alpine AS development
WORKDIR /app
COPY pom.xml .
COPY src ./src
CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.profiles=dev"]

# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Production stage
FROM eclipse-temurin:21-jre-alpine AS production
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "app.jar"]
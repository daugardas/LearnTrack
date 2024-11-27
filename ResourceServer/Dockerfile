FROM eclipse-temurin:21-jdk-alpine as builder
WORKDIR /opt/app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -Dmaven.test.skip=true -Dspring.datasource.url=jdbc:postgresql://none
COPY ./src ./src
RUN ./mvnw clean install -DskipTests -Dspring.datasource.url=jdbc:postgresql://none

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /opt/app
EXPOSE 8080
COPY --from=builder /opt/app/target/*.jar /opt/app/*.jar
ENTRYPOINT ["java","-Dspring.profiles.active=prod", "-jar", "/opt/app/*.jar" ]
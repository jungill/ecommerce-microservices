FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY product-service/pom.xml product-service/
COPY order-service/pom.xml order-service/
COPY gateway-service/pom.xml gateway-service/

RUN mvn dependency:go-offline -pl gateway-service -am

COPY gateway-service/src gateway-service/src
RUN mvn package -pl gateway-service -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/gateway-service/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
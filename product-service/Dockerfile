FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY product-service/pom.xml product-service/
COPY order-service/pom.xml order-service/
COPY gateway-service/pom.xml gateway-service/

RUN mvn dependency:go-offline -pl product-service -am

COPY product-service/src product-service/src
RUN mvn package -pl product-service -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/product-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
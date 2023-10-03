FROM openjdk:17-jdk-alpine
ADD target/product-service-0.0.1-SNAPSHOT.jar /app/target/product-service-0.0.1-SNAPSHOT.jar
LABEL authors="med"
EXPOSE 9090
ENTRYPOINT ["java","-jar","/app/target/product-service-0.0.1-SNAPSHOT.jar"]
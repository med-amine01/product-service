FROM openjdk:17-oracle

ADD target/product-service-0.0.1-SNAPSHOT.jar product-service-0.0.1-SNAPSHOT.jar

LABEL authors="med"

# first thing should be executable when container launched
ENTRYPOINT ["java","-jar","product-service-0.0.1-SNAPSHOT.jar"]
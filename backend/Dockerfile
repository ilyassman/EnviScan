FROM openjdk:22-slim

WORKDIR /app

COPY target/*.jar /app/app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
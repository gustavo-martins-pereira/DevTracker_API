FROM eclipse-temurin:25-jre

WORKDIR /app

COPY target/devtrackerapi-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
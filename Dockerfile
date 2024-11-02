FROM openjdk:21.0.2-jre-slim
WORKDIR /app
COPY ./mein-programm.jar /app/
CMD ["java", "-jar", "app.jar"]
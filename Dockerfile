FROM openjdk:21-jdk-slim
COPY bank/build/libs/bankJava.jar /app/bankJava.jar
ENTRYPOINT ["java", "-jar", "/app/bankJava.jar"]
FROM openjdk:19-jdk

WORKDIR /app

COPY ./target/musicFinder-0.0.1-SNAPSHOT.jar /app/musicFinder.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/musicFinder.jar"]
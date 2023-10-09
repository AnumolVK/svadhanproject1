FROM openjdk:17

COPY target/collection-1.1.0-SNAPSHOT.jar collection.jar

EXPOSE 8809
ENTRYPOINT ["java", "-jar", "/collection.jar"]

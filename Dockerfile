FROM azul/zulu-openjdk:11.0.7

EXPOSE 8080

RUN mkdir /app
WORKDIR /app
ADD build/libs/javap-server.jar /app/javap-server.jar

CMD ["java", "-jar", "javap-server.jar"]

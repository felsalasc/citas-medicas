FROM eclipse-temurin:22-jdk AS buildstage

RUN apt-get update && apt-get install -y maven

WORKDIR /app

COPY pom.xml .
COPY src /app/src
COPY wallet /app/wallet

ENV TNS_ADMIN=/app/wallet

RUN mvn clean package -DskipTests


FROM eclipse-temurin:22-jdk

WORKDIR /app

COPY --from=buildstage /app/target/*.jar /app/app.jar
COPY wallet /app/wallet

ENV TNS_ADMIN=/app/wallet

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
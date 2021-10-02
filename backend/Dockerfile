FROM maven:3.8.1-jdk-11 AS MAVEN_BUILD

COPY pom.xml /build/
COPY src /build/src/

WORKDIR /build/
RUN mvn clean package -DskipTests

FROM openjdk:11

WORKDIR /app

COPY --from=MAVEN_BUILD /build/target/tripbuddyc-1.0.jar /app/

ENTRYPOINT ["java", "-jar", "tripbuddyc-1.0.jar"]
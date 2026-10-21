# Java 21 mühiti
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Maven wrapper-i kopyalayırıq
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# Layihə kodunu kopyalayıb yığırıq (testləri keçirik)
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Tətbiqi işə salırıq
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
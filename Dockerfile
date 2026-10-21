# Mərhələ 1: Layihəni yığmaq (build)
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Bütün layihə fayllarını kopyalayırıq
COPY . .

# Maven-i işə salaraq JAR faylını yaradırıq
RUN mvn clean package -DskipTests

# Mərhələ 2: Tətbiqi işə salmaq
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Yığılmış JAR faylını birinci mərhələdən götürürük
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
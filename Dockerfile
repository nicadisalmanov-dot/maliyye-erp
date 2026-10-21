# Mərhələ 1: Layihəni yığmaq (build)
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Bütün layihə fayllarını kopyalayırıq
COPY . .

# Əgər layihə iç-içə qovluqdadırsa, həmin qovluğa keçib Maven-i işə salırıq
# MaliyyeApp qovluğu yoxdursa, kök qovluqda işləyir
RUN if [ -d "MaliyyeApp" ]; then cd MaliyyeApp; fi && mvn clean package -DskipTests

# Mərhələ 2: Tətbiqi işə salmaq
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Yığılmış JAR faylını tapıb kopyalayırıq
COPY --from=build /app/MaliyyeApp/target/*.jar app.jar 2>/dev/null || COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
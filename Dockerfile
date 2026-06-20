# Stage 1: Build mã nguồn thành file JAR
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Build project và bỏ qua test để quá trình deploy nhanh hơn
RUN mvn clean package -DskipTests

# Stage 2: Thiết lập môi trường chạy ứng dụng Spring Boot
FROM openjdk:17-jdk-slim
WORKDIR /app
# Lấy file .jar đã build ở Stage 1 đem qua đây
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
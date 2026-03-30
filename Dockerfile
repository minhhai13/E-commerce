# Giai đoạn 1: Build
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Giai đoạn 2: Run
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Tạo thư mục lưu ảnh và cấp quyền ghi cho ứng dụng
RUN mkdir -p /app/uploads && chmod 777 /app/uploads

# Chỉ cần COPY một lần duy nhất
COPY --from=build /app/target/*.jar app.jar

# Port mặc định thường là 8080
EXPOSE 8080

ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-XX:TieredStopAtLevel=1", "-jar", "app.jar"]

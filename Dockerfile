# Giai đoạn 1: Build ứng dụng bằng Maven
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app

# Copy toàn bộ mã nguồn vào container
COPY . .

# Chạy lệnh build của Maven để tạo file jar (bỏ qua chạy test để nhanh hơn)
RUN mvn clean package -DskipTests

# Giai đoạn 2: Chạy ứng dụng
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

RUN mkdir -p /app/uploads && chmod 777 /app/uploads

COPY --from=build /app/target/*.jar app.jar

# Copy file jar đã build từ giai đoạn 1 sang giai đoạn 2
COPY --from=build /app/target/*.jar app.jar

# Lệnh khởi chạy
ENTRYPOINT ["java", "-jar", "app.jar"]


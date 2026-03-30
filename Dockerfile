# Sử dụng JDK 17 (hoặc phiên bản bạn đang dùng như 11, 21)
FROM eclipse-temurin:17-jdk-alpine

# Tạo thư mục làm việc
WORKDIR /app

# Copy file jar từ thư mục target vào container (đổi tên cho dễ gọi là app.jar)
# Lưu ý: Thay đổi đường dẫn nếu bạn dùng Gradle (build/libs/*.jar)
COPY target/*.jar app.jar

# Lệnh để chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
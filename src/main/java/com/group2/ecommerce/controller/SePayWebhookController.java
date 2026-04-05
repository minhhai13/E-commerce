package com.group2.ecommerce.controller;

import com.group2.ecommerce.dto.SePayWebhookPayload;
import com.group2.ecommerce.entity.Order;
import com.group2.ecommerce.entity.enums.OrderStatus;
import com.group2.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/sepay-webhook")
@RequiredArgsConstructor
public class SePayWebhookController {

    private final OrderRepository orderRepository;

    // Khóa do SePay cung cấp
    @Value("${sepay.api-key}")
    private String apiKey;

    @Value("${sepay.exchange-rate}")
    private int exchangeRate;

    @GetMapping("/check-status/{orderId}")
    public org.springframework.http.ResponseEntity<String> checkOrderStatus(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            return org.springframework.http.ResponseEntity.ok(order.getStatus().name());
        }
        return org.springframework.http.ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<String> handleSePayWebhook(
            @RequestBody SePayWebhookPayload payload,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        // Kiểm tra Token API có đúng không (SePay gửi header chứa Apikey ...)
        if (authHeader == null || !authHeader.contains(apiKey)) {
            // Trả về 401 nếu khóa không hợp lệ
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String content = payload.getContent();
        if (content == null) {
            // Vẫn phải trả về HTTP 200 để SePay không gửi lại payload
            return ResponseEntity.ok("No content");
        }

        // Bỏ qua nếu không phải là giao dịch chuyển tiền vào (transferType = "in")
        if (!"in".equalsIgnoreCase(payload.getTransferType())) {
            return ResponseEntity.ok("Not an IN transfer");
        }

        // Tìm chuỗi "DH" kèm theo số phía sau. Ví dụ đoạn text: "NGUYEN VAN A CHUYEN
        // TIEN DH123" -> tìm "DH123"
        Pattern pattern = Pattern.compile("DH(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            Long orderId;
            try {
                // Tách lấy số 123
                orderId = Long.parseLong(matcher.group(1));
            } catch (NumberFormatException e) {
                return ResponseEntity.ok("Invalid Order ID format");
            }

            // Gọi database lôi ra đơn hàng theo số ID
            Order order = orderRepository.findById(orderId).orElse(null);

            // Nếu có đơn hàng tồn tại
            if (order != null) {
                // Đối soát xem số tiền có đủ không
                long expectedVndAmount = order.getTotalAmount().multiply(new java.math.BigDecimal(exchangeRate)).longValue();

                if (expectedVndAmount == payload.getTransferAmount()) {
                    // Nếu còn đang chờ thanh toán
                    if (order.getStatus() == OrderStatus.WAITING_PAYMENT) {
                        order.setStatus(OrderStatus.WAITING_CONFIRMATION);
                        orderRepository.save(order);
                    }
                }
            }
        }

        // Luôn trả về 200 OK
        return ResponseEntity.ok("Success");
    }
}

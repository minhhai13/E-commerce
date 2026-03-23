package com.group2.ecommerce.dto;

import java.math.BigDecimal;

public class CartItem {

    private Long productId;
    private String productName;
    private String imageName;
    private BigDecimal price;
    private int quantity;
    private int stockQuantity;

    public CartItem() {}

    public CartItem(Long productId, String productName, String imageName, BigDecimal price, int quantity, int stockQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.imageName = imageName;
        this.price = price;
        this.quantity = quantity;
        this.stockQuantity = stockQuantity;
    }

    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    // Getters & Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
}

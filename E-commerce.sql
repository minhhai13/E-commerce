-- ============================================================
-- REFACTORED E-COMMERCE DATABASE (100% SRS COMPLIANT)
-- ============================================================
CREATE DATABASE E_COMMERCE_FINAL;
USE E_COMMERCE_FINAL;

-- 1. TABLE: categories (Quản lý danh mục - Hỗ trợ UC06)
CREATE TABLE categories (
    id              BIGINT          IDENTITY(1,1)   NOT NULL,
    name            NVARCHAR(100)                   NOT NULL,
    description     NVARCHAR(255)                   NULL,
    parent_id       BIGINT                          NULL, 
    is_active       BIT                             NOT NULL    DEFAULT 1,

    CONSTRAINT PK_categories PRIMARY KEY (id),
    CONSTRAINT FK_categories_parent FOREIGN KEY (parent_id) REFERENCES categories(id)
);

-- 2. TABLE: users (Quản lý tài khoản - Đáp ứng UC01, UC11, UC12)
-- Role Mapping: 1: ADMIN, 2: STAFF, 3: CUSTOMER
CREATE TABLE users (
    id              BIGINT          IDENTITY(1,1)   NOT NULL,
    full_name       NVARCHAR(100)                   NOT NULL,
    email           NVARCHAR(150)                   NOT NULL, -- BR-01: Unique email
    password_hash   NVARCHAR(255)                   NOT NULL,
    phone           NVARCHAR(20)                    NULL,
    role_id         TINYINT                         NOT NULL    DEFAULT 3, 
    is_active       BIT                             NOT NULL    DEFAULT 1, -- BR-02: Deactivated accounts
    created_at      DATETIME2                       NOT NULL    DEFAULT GETDATE(),

    CONSTRAINT PK_users PRIMARY KEY (id),
    CONSTRAINT UQ_users_email UNIQUE (email)
);

-- 3. TABLE: user_addresses (Sổ địa chỉ - Đáp ứng UC10)
-- Lưu nhiều địa chỉ cho Customer, hỗ trợ BR-19
CREATE TABLE user_addresses (
    id              BIGINT          IDENTITY(1,1)   NOT NULL,
    user_id         BIGINT                          NOT NULL,
    recipient_name  NVARCHAR(100)                   NOT NULL,
    phone           NVARCHAR(20)                    NOT NULL,
    address_detail  NVARCHAR(500)                   NOT NULL,
    is_default      BIT                             NOT NULL    DEFAULT 0,

    CONSTRAINT PK_user_addresses PRIMARY KEY (id),
    CONSTRAINT FK_user_addresses_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 4. TABLE: products (Danh mục sản phẩm - Đáp ứng UC02, UC05, UC06)
CREATE TABLE products (
    id              BIGINT          IDENTITY(1,1)   NOT NULL,
    category_id     BIGINT                          NULL,
    name            NVARCHAR(200)                   NOT NULL,
    description     NVARCHAR(MAX)                   NULL,
    price           DECIMAL(15, 2)                  NOT NULL, -- BR-03: Must be positive
    stock_quantity  INT                             NOT NULL    DEFAULT 0, -- Quản lý bởi UC05
    image_name      NVARCHAR(255)                   NULL,
    is_active       BIT                             NOT NULL    DEFAULT 1, -- BR-12: Only active visible
    created_at      DATETIME2                       NOT NULL    DEFAULT GETDATE(),

    CONSTRAINT PK_products PRIMARY KEY (id),
    CONSTRAINT FK_products_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    CONSTRAINT CHK_products_price CHECK (price > 0)
);

-- 5. TABLE: coupons (Quản lý khuyến mãi - Đáp ứng UC03)
CREATE TABLE coupons (
    id              BIGINT          IDENTITY(1,1)   NOT NULL,
    code            NVARCHAR(50)                    NOT NULL, -- BR-05: Unique code
    discount_type   NVARCHAR(20)                    NOT NULL, -- 'FIXED' or 'PERCENTAGE'
    discount_value  DECIMAL(15, 2)                  NOT NULL, -- BR-06: Positive
    usage_limit     INT                             NOT NULL, -- BR-07: Limit check
    used_count      INT                             NOT NULL    DEFAULT 0,
    valid_from      DATETIME2                       NOT NULL,
    valid_until     DATETIME2                       NOT NULL, -- BR-07: Expiry check
    is_active       BIT                             NOT NULL    DEFAULT 1,

    CONSTRAINT PK_coupons PRIMARY KEY (id),
    CONSTRAINT UQ_coupons_code UNIQUE (code),
    CONSTRAINT CHK_coupons_value CHECK (discount_value > 0)
);

-- 6. TABLE: orders (Quản lý đơn hàng - Đáp ứng UC04, UC08, UC09)
-- Status Mapping (BR-08): 1: Waiting Confirmation, 2: Waiting Pickup, 3: In Transit, 4: Completed, 0: Cancelled
CREATE TABLE orders (
    id                BIGINT          IDENTITY(1,1)   NOT NULL,
    user_id           BIGINT                          NOT NULL,
    coupon_id         BIGINT                          NULL, -- Lưu coupon đã áp dụng (UC08)
    status_id         TINYINT                         NOT NULL    DEFAULT 1,
    
    subtotal          DECIMAL(15, 2)                  NOT NULL, -- Tổng tiền trước giảm
    discount_amount   DECIMAL(15, 2)                  NOT NULL    DEFAULT 0,
    total_amount      DECIMAL(15, 2)                  NOT NULL, -- Tổng tiền sau giảm

    -- Snapshot thông tin giao hàng (UC08)
    shipping_name     NVARCHAR(100)                   NOT NULL,
    shipping_phone    NVARCHAR(20)                    NOT NULL,
    shipping_address  NVARCHAR(500)                   NOT NULL,

    created_at        DATETIME2                       NOT NULL    DEFAULT GETDATE(),
    updated_at        DATETIME2                       NOT NULL    DEFAULT GETDATE(),

    CONSTRAINT PK_orders PRIMARY KEY (id),
    CONSTRAINT FK_orders_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT FK_orders_coupon FOREIGN KEY (coupon_id) REFERENCES coupons(id)
);

GO
CREATE TRIGGER trg_orders_UpdateTimestamp
ON orders
AFTER UPDATE
AS
BEGIN
    UPDATE orders
    SET updated_at = GETDATE()
    FROM inserted
    WHERE orders.id = inserted.id;
END;

-- 7. TABLE: order_items (Chi tiết đơn hàng - Đáp ứng UC08)
CREATE TABLE order_items (
    id              BIGINT          IDENTITY(1,1)   NOT NULL,
    order_id        BIGINT                          NOT NULL,
    product_id      BIGINT                          NULL, -- Giữ lịch sử dù SP bị xóa
    product_name    NVARCHAR(200)                   NOT NULL, -- Snapshot tên tại thời điểm mua
    unit_price      DECIMAL(15, 2)                  NOT NULL, -- Snapshot giá tại thời điểm mua
    quantity        INT                             NOT NULL,
    
    CONSTRAINT PK_order_items PRIMARY KEY (id),
    CONSTRAINT FK_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT FK_order_items_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE SET NULL
);
GO
USE E_COMMERCE_FINAL;
GO

-- =========================================================================
-- 1. INSERT CATEGORIES (Shoe Store)
-- =========================================================================

-- Root Categories
INSERT INTO categories (name, description, parent_id, is_active)
VALUES 
(N'Men''s Shoes', N'Sneakers, Boots, Loafers, and Running shoes for men', NULL, 1),
(N'Women''s Shoes', N'Sneakers, Heels, Flats, and Running shoes for women', NULL, 1),
(N'Kids', N'Shoes for boys and girls', NULL, 1);

-- Get IDs of Root Categories
DECLARE @MensId BIGINT = (SELECT id FROM categories WHERE name = N'Men''s Shoes');
DECLARE @WomensId BIGINT = (SELECT id FROM categories WHERE name = N'Women''s Shoes');
DECLARE @KidsId BIGINT = (SELECT id FROM categories WHERE name = N'Kids');

-- Subcategories for Men
INSERT INTO categories (name, description, parent_id, is_active)
VALUES 
(N'Men''s Sneakers', N'Everyday lifestyle and casual sneakers.', @MensId, 1),
(N'Men''s Running', N'High performance running and training shoes.', @MensId, 1),
(N'Men''s Boots', N'Sturdy boots for work and fashion.', @MensId, 1);

-- Subcategories for Women
INSERT INTO categories (name, description, parent_id, is_active)
VALUES 
(N'Women''s Sneakers', N'Lifestyle and fashion sneakers for women.', @WomensId, 1),
(N'Women''s Running', N'Premium running shoes for women.', @WomensId, 1),
(N'Heels & Flats', N'Elegant heels, wedges, and comfortable flats.', @WomensId, 1);

-- Subcategories for Kids
INSERT INTO categories (name, description, parent_id, is_active)
VALUES 
(N'Kids'' Sneakers', N'Comfortable and durable sneakers for children.', @KidsId, 1),
(N'Kids'' Sandals', N'Lightweight and breathable summer sandals.', @KidsId, 1);
GO

-- =========================================================================
-- 2. INSERT PRODUCTS (Shoe Store - .jpg images)
-- =========================================================================

-- Declare variable to lookup Subcategory IDs easily
DECLARE @MensSneakers BIGINT = (SELECT id FROM categories WHERE name = N'Men''s Sneakers');
DECLARE @MensRunning BIGINT = (SELECT id FROM categories WHERE name = N'Men''s Running');
DECLARE @MensBoots BIGINT = (SELECT id FROM categories WHERE name = N'Men''s Boots');
DECLARE @WomensSneakers BIGINT = (SELECT id FROM categories WHERE name = N'Women''s Sneakers');
DECLARE @WomensRunning BIGINT = (SELECT id FROM categories WHERE name = N'Women''s Running');
DECLARE @WomensHeels BIGINT = (SELECT id FROM categories WHERE name = N'Heels & Flats');
DECLARE @KidsSneakers BIGINT = (SELECT id FROM categories WHERE name = N'Kids'' Sneakers');
DECLARE @KidsSandals BIGINT = (SELECT id FROM categories WHERE name = N'Kids'' Sandals');

INSERT INTO products (category_id, name, description, price, stock_quantity, image_name, is_active, created_at)
VALUES 

-- Men's Sneakers
(@MensSneakers, N'Nike Air Jordan 1 High', N'Classic retro basketball sneaker in Chicago colorway. Premium leather upper.', 180.00, 35, N'air_jordan_1_high.jpg', 1, GETDATE()),
(@MensSneakers, N'Nike Dunk Low Retro', N'White and Black "Panda" colorway. A street styling staple.', 115.00, 150, N'nike_dunk_low.jpg', 1, GETDATE()),
(@MensSneakers, N'New Balance 550', N'Vintage basketball oxford featuring a clean leather upper and rubber outsole.', 120.00, 85, N'nb_550.jpg', 1, GETDATE()),
(@MensSneakers, N'Converse Chuck 70', N'Upgraded vintage silhouette with heavier canvas and cushioned insole.', 90.00, 200, N'chuck_70.jpg', 1, GETDATE()),

-- Men's Running
(@MensRunning, N'Adidas Ultraboost Light', N'Lightest Ultraboost ever. Epic energy with every stride.', 190.00, 60, N'ultraboost_light.jpg', 1, GETDATE()),
(@MensRunning, N'Asics Gel-Kayano 30', N'Maximum support running shoe with updated 4D GUIDANCE SYSTEM.', 160.00, 45, N'gel_kayano_30.jpg', 1, GETDATE()),

-- Men's Boots
(@MensBoots, N'Timberland Premium 6-Inch', N'The original waterproof work boot. Rust-proof hardware.', 210.00, 40, N'timberland_6in.jpg', 1, GETDATE()),
(@MensBoots, N'Dr. Martens 1460', N'Iconic 8-eye boot in smooth black leather with yellow stitching.', 170.00, 55, N'dr_martens_1460.jpg', 1, GETDATE()),

-- Women's Sneakers
(@WomensSneakers, N'Nike Air Force 1 ''07', N'Crisp leather, bold details and the perfect amount of flash.', 115.00, 120, N'air_force_1_w.jpg', 1, GETDATE()),
(@WomensSneakers, N'Puma Mayze Classic', N'Stacked sole and contrasting rubber tooling for an edgy look.', 100.00, 75, N'puma_mayze.jpg', 1, GETDATE()),
(@WomensSneakers, N'Vans Old Skool Platform', N'Classic sidestripe skate shoe with chunky platform soles.', 75.00, 140, N'vans_old_skool_plat.jpg', 1, GETDATE()),

-- Women's Running
(@WomensRunning, N'HOKA Clifton 9', N'Lighter and more cushioned everyday mile-maker running shoe.', 145.00, 80, N'hoka_clifton_9.jpg', 1, GETDATE()),
(@WomensRunning, N'Nike Pegasus 40', N'A springy ride for every run. Familiar, just-for-you feel.', 130.00, 95, N'nike_pegasus_40.jpg', 1, GETDATE()),

-- Heels & Flats
(@WomensHeels, N'Classic Stiletto Pump', N'Elegant black patent leather pump with a 4-inch heel.', 120.00, 30, N'black_stiletto.jpg', 1, GETDATE()),
(@WomensHeels, N'Ballet Flat Leather', N'Everyday comfort round-toe flat in soft nude leather.', 85.00, 65, N'nude_ballet_flat.jpg', 1, GETDATE()),

-- Kids' Sneakers
(@KidsSneakers, N'Nike Air Force 1 Kids', N'Classic low-top comfort for the little ones.', 65.00, 50, N'nike_af1_kids.jpg', 1, GETDATE()),
(@KidsSneakers, N'Adidas Superstar Kids', N'Iconic shell toe sneakers in kids sizing.', 60.00, 70, N'adidas_superstar_kids.jpg', 1, GETDATE()),

-- Kids' Sandals
(@KidsSandals, N'Crocs Classic Clog Kids', N'Easy to wear, easy to clean.', 35.00, 100, N'crocs_kids.jpg', 1, GETDATE()),
(@KidsSandals, N'Nike Sunray Protect 3', N'Fun and flexible for warm weather wear.', 40.00, 45, N'nike_sunray_kids.jpg', 1, GETDATE());

GO

-- =========================================================================
-- Verification Query
-- =========================================================================
SELECT p.id as ProductId, p.name as ProductName, p.image_name, p.price, p.stock_quantity, c.name as CategoryName
FROM products p
LEFT JOIN categories c ON p.category_id = c.id
ORDER BY c.name, p.name;
GO

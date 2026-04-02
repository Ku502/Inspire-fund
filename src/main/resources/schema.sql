-- =============================================
-- InspireFund Database Schema
-- Run this in MySQL before starting the app
-- =============================================

CREATE DATABASE IF NOT EXISTS inspirefund_db
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE inspirefund_db;

-- USERS
CREATE TABLE IF NOT EXISTS users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100)  NOT NULL,
    email         VARCHAR(150)  NOT NULL UNIQUE,
    password_hash VARCHAR(255)  NOT NULL,
    phone         VARCHAR(20),
    role          ENUM('DONOR','ADMIN') NOT NULL DEFAULT 'DONOR',
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- CAMPAIGNS
CREATE TABLE IF NOT EXISTS campaigns (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    title              VARCHAR(200)   NOT NULL,
    short_desc         VARCHAR(300),
    description        TEXT,
    category           VARCHAR(50),
    emoji              VARCHAR(10),
    goal               DECIMAL(12,2)  NOT NULL,
    raised             DECIMAL(12,2)  DEFAULT 0.00,
    donor_count        INT            DEFAULT 0,
    end_date           DATE,
    status             ENUM('PENDING','ACTIVE','CLOSED','COMPLETED') DEFAULT 'PENDING',
    organizer_name     VARCHAR(100),
    organizer_contact  VARCHAR(150),
    created_by         BIGINT,
    created_at         DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- DONATIONS
CREATE TABLE IF NOT EXISTS donations (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id              BIGINT,
    campaign_id          BIGINT         NOT NULL,
    amount               DECIMAL(10,2)  NOT NULL,
    razorpay_order_id    VARCHAR(100),
    razorpay_payment_id  VARCHAR(100),
    razorpay_signature   VARCHAR(300),
    payment_status       ENUM('PENDING','SUCCESS','FAILED') DEFAULT 'PENDING',
    anonymous            BOOLEAN DEFAULT FALSE,
    created_at           DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id)    REFERENCES users(id)     ON DELETE SET NULL,
    FOREIGN KEY (campaign_id) REFERENCES campaigns(id) ON DELETE CASCADE
);

-- =============================================
-- SEED DATA (for development & demo)
-- =============================================

-- Admin user (password: Admin@123)
INSERT INTO users (name, email, password_hash, role) VALUES
('Admin User', 'admin@inspirefund.in',
 '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN');

-- Donor user (password: Donor@123)
INSERT INTO users (name, email, password_hash, phone, role) VALUES
('Priya Sharma', 'priya@example.com',
 '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+91 98765 43210', 'DONOR');

-- Sample campaigns
INSERT INTO campaigns (title, short_desc, description, category, emoji, goal, raised, donor_count, end_date, status, organizer_name, organizer_contact, created_by) VALUES
(
  'Surgery for Riya, age 7',
  'Help 7-year-old Riya get life-saving cardiac surgery.',
  'Riya is a bright seven-year-old from Raipur who was recently diagnosed with a congenital heart defect. The surgery costs ₹1,20,000 and her family — daily-wage workers — cannot afford it. Your donation goes directly to Narayana Hospital''s patient trust fund.',
  'health', '❤️', 120000.00, 110400.00, 891,
  DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'ACTIVE',
  'Rajesh Kumar', 'rajesh.k@example.com', 1
),
(
  'Books & Bags for Bastar',
  'School supplies for 200 tribal children in Bastar, Chhattisgarh.',
  'Help us provide textbooks, bags, and stationery to 200 children from tribal communities in the remote Bastar district of Chhattisgarh. Many walk 5km to school with no supplies at all.',
  'education', '📚', 120000.00, 75600.00, 312,
  DATE_ADD(CURDATE(), INTERVAL 14 DAY), 'ACTIVE',
  'Sunita Netam', 'sunita.netam@ngo.org', 1
),
(
  'Assam Flood Relief 2025',
  'Emergency aid for thousands displaced by floods in Assam.',
  'Unprecedented flooding has displaced over 10,000 families in Assam. Funds go directly toward food packets, tarpaulins, drinking water, and medicines distributed by our ground team.',
  'disaster', '🌊', 500000.00, 340000.00, 2140,
  DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'ACTIVE',
  'Relief India NGO', 'relief@reliefindia.org', 1
),
(
  'Dog Rescue Shelter — Pune',
  'Building a new shelter for 150 rescued street dogs.',
  'Our existing shelter in Pune is beyond capacity. We need to build a new facility with 150 kennels, a medical bay, and an adoption center. Every dog here has been rescued from abuse or abandonment.',
  'animal', '🐕', 200000.00, 45000.00, 187,
  DATE_ADD(CURDATE(), INTERVAL 21 DAY), 'PENDING',
  'PawsUp Foundation', 'pawsup@shelter.org', 1
),
(
  'Digital Literacy for Rural Girls',
  'Laptops and internet for 100 girls in rural Rajasthan.',
  'We are providing refurbished laptops, internet dongles, and a 6-month digital literacy training program to 100 girls from villages in Ajmer district, Rajasthan.',
  'education', '💻', 150000.00, 92000.00, 430,
  DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'ACTIVE',
  'ShikshaNet', 'contact@shikshanet.org', 1
),
(
  'Cancer treatment — Arjun, 34',
  'Help Arjun afford chemotherapy after a sudden cancer diagnosis.',
  'Arjun was a healthy 34-year-old teacher until he was diagnosed with Stage 2 lymphoma last month. He needs 6 cycles of chemotherapy costing ₹2,50,000. His family has exhausted savings and needs your help.',
  'health', '🎗️', 250000.00, 68000.00, 320,
  DATE_ADD(CURDATE(), INTERVAL 45 DAY), 'ACTIVE',
  'Meena Sharma', 'meena.s@example.com', 1
);

-- Sample donations
INSERT INTO donations (user_id, campaign_id, amount, razorpay_order_id, razorpay_payment_id, payment_status, anonymous) VALUES
(2, 1, 2000.00, 'order_demo_001', 'pay_demo_001', 'SUCCESS', FALSE),
(2, 2, 1000.00, 'order_demo_002', 'pay_demo_002', 'SUCCESS', FALSE),
(2, 3, 5000.00, 'order_demo_003', 'pay_demo_003', 'SUCCESS', TRUE);

-- Indexes for performance
CREATE INDEX idx_campaigns_status   ON campaigns(status);
CREATE INDEX idx_campaigns_category ON campaigns(category);
CREATE INDEX idx_donations_campaign ON donations(campaign_id);
CREATE INDEX idx_donations_user     ON donations(user_id);
CREATE INDEX idx_donations_order    ON donations(razorpay_order_id);

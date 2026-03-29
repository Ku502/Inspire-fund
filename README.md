# InspireFund 🔥
### A modern charity crowdfunding platform — built with HTML/CSS/JS + Spring Boot + MySQL

---

## Project Structure

```
InspireFund/
├── frontend/
│   ├── index.html        ← Home page with campaign cards
│   ├── campaign.html     ← Campaign detail + donate
│   ├── login.html        ← Sign in
│   ├── register.html     ← Create account
│   └── admin.html        ← Admin dashboard
│
└── inspirefund-backend/
    ├── pom.xml
    └── src/main/
        ├── java/com/inspirefund/
        │   ├── InspireFundApplication.java
        │   ├── config/
        │   │   └── SecurityConfig.java
        │   ├── controller/
        │   │   ├── AuthController.java
        │   │   ├── CampaignController.java
        │   │   └── DonationController.java
        │   ├── dto/
        │   │   ├── RegisterRequest.java
        │   │   └── LoginRequest.java
        │   ├── entity/
        │   │   ├── User.java
        │   │   ├── Campaign.java
        │   │   └── Donation.java
        │   ├── repository/
        │   │   ├── UserRepository.java
        │   │   ├── CampaignRepository.java
        │   │   └── DonationRepository.java
        │   └── security/
        │       ├── JwtUtils.java
        │       └── JwtAuthFilter.java
        └── resources/
            ├── application.properties
            └── schema.sql
```

---

## Setup Guide

### Step 1 — MySQL Database

```sql
-- Open MySQL Workbench or terminal and run:
mysql -u root -p < src/main/resources/schema.sql
```

This creates the `inspirefund_db` database with all tables and seed data.

**Demo credentials (seeded):**
- Admin: `admin@inspirefund.in` / `Admin@123`
- Donor: `priya@example.com` / `Donor@123`

---

### Step 2 — Configure application.properties

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.password=YOUR_MYSQL_PASSWORD

razorpay.key.id=rzp_test_YOUR_KEY_ID
razorpay.key.secret=YOUR_KEY_SECRET
```

Get Razorpay test keys from: https://dashboard.razorpay.com → Settings → API Keys → Test Mode

---

### Step 3 — Run Spring Boot

```bash
cd inspirefund-backend
mvn spring-boot:run
```

Backend runs on: `http://localhost:8080`

---

### Step 4 — Run Frontend

Open `frontend/index.html` in a browser, or use VS Code Live Server (recommended — avoids CORS issues):

```bash
# Install VS Code Live Server extension, then right-click index.html → Open with Live Server
# Default: http://127.0.0.1:5500
```

---

## API Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/register` | Public | Register new donor |
| POST | `/api/auth/login` | Public | Login → returns JWT |
| GET | `/api/campaigns` | Public | List active campaigns |
| GET | `/api/campaigns/{id}` | Public | Campaign detail |
| POST | `/api/campaigns` | Admin | Create campaign |
| PUT | `/api/campaigns/{id}` | Admin | Edit campaign |
| PATCH | `/api/campaigns/{id}/close` | Admin | Close campaign |
| GET | `/api/campaigns/admin/all` | Admin | All campaigns |
| POST | `/api/donations/create-order` | Donor | Create Razorpay order |
| POST | `/api/donations/verify` | Donor | Verify payment + update DB |
| GET | `/api/donations/my` | Donor | My donation history |
| GET | `/api/donations/campaign/{id}` | Public | Donations for campaign |

---

## Razorpay Payment Flow

```
1. User clicks "Donate ₹500"
2. Frontend → POST /api/donations/create-order
3. Backend creates Razorpay order, saves PENDING donation
4. Frontend opens Razorpay checkout modal
5. User pays via UPI / card / netbanking
6. Razorpay calls handler with {order_id, payment_id, signature}
7. Frontend → POST /api/donations/verify
8. Backend verifies HMAC-SHA256 signature
9. Backend marks donation SUCCESS, updates campaign.raised
10. User sees success message ✓
```

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | HTML5, CSS3, Vanilla JavaScript |
| Backend | Java 17, Spring Boot 3.2, Spring Security |
| Auth | JWT (JJWT library) + BCrypt |
| Database | MySQL 8.0 with JPA/Hibernate |
| Payments | Razorpay Java SDK |

---


# InspireFund 
### A modern charity crowdfunding platform — built with HTML/CSS/JS + Spring Boot + MySQL



## Project Structure


InspireFund
├── frontend
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



















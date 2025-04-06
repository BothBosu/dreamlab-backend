# Dream Lab — Backend

Dream Lab is a community-driven AI image generation platform. This backend repo is built with Spring Boot and serves REST APIs for user authentication, image generation, storage, and community interactions.

## 🎯 Core Functionalities

### ✅ User Management
- Register/Login system
- Spring Security authentication
- Password change & profile picture management

### ✅ AI Image Generation
- Integration with [getimg.ai](https://getimg.ai/) API
- Customizable image prompt settings
- Metadata storage (prompt, image url, user id)

### ✅ Community & Storage
- Like system for images
- Public gallery feed
- Image metadata storage in SQL
- Secure image hosting via AWS S3

### ✅ Dashboard
- User-specific dashboard for managing generated images

## 🧰 Tech Stack

- ☕ Java 17+
- 🧩 Spring Boot 3
- 🔐 Spring Security
- 🧠 getimg.ai API
- 🗃️ MySQL Database
- ☁️ AWS S3 for storage
- 🐳 Dockerized deployment on Ubuntu (AWS Lightsail)

## 📁 Project Structure

```bash
src/
└── main/
    ├── java/com/muic/ssc/backend/
    │   ├── Config/         # Security and application config
    │   ├── Controller/     # REST API controllers
    │   ├── Entity/         # JPA entities
    │   ├── Model/          # Request/Response models
    │   ├── Repository/     # Spring Data repositories
    │   ├── Service/        # Business logic services
    │   └── Utils/          # Utility classes
    └── resources/          # application.properties, static files
```

## 🧑‍💻 Developer Contributions

- **Pitipat**: Project management, landing page, login & register page, deployment
- **Pawin**: Image generation page, user management system
- **Szuchihsu**: Gallery, dashboard, AWS S3 integration

🌍 Frontend
Connects to [dreamlab-frontend](https://github.com/BothBosu/dreamlab-frontend)

## 🛠 Running the Project

### Prerequisites

- Java 17+
- Docker
- MySQL
- AWS credentials (for S3)

# Start backend service

```bash
mvn wrapper:wrapper
./mvnw spring-boot:run
```

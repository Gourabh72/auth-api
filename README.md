# 🔐 Spring Boot JWT Authentication API

A production-ready REST API for user registration and JWT-based authentication built with Spring Boot 3, Spring Security 6, and JJWT.

---

## 📁 Project Structure

```
src/main/java/com/auth/api/
├── AuthApiApplication.java
├── config/
│   ├── SecurityConfig.java          ← Spring Security + filter chain
│   └── GlobalExceptionHandler.java  ← Centralized error handling
├── controller/
│   └── AuthController.java          ← REST endpoints
├── dto/
│   ├── RegisterRequest.java
│   ├── LoginRequest.java
│   ├── AuthResponse.java
│   └── ApiResponse.java             ← Generic wrapper
├── entity/
│   ├── User.java
│   └── Role.java                    ← ROLE_USER | ROLE_ADMIN | ROLE_MODERATOR
├── repository/
│   └── UserRepository.java
├── security/
│   ├── JwtUtils.java                ← Token generation & validation
│   ├── JwtAuthenticationFilter.java ← Per-request JWT check
│   ├── UserPrincipal.java           ← UserDetails impl
│   └── UserDetailsServiceImpl.java  ← Loads user by username OR email
└── service/
    └── AuthService.java             ← Business logic
```

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+

### Run (H2 in-memory DB — no setup needed)
```bash
cd auth-api
mvn spring-boot:run
```

The server starts on **http://localhost:8080**

### H2 Console (dev only)
Open **http://localhost:8080/h2-console**
- JDBC URL: `jdbc:h2:mem:authdb`
- Username: `sa` | Password: *(empty)*

---

## 📡 API Endpoints

| Method | URL               | Auth Required | Description              |
|--------|-------------------|---------------|--------------------------|
| POST   | `/api/auth/register` | ❌ Public  | Register a new user      |
| POST   | `/api/auth/login`    | ❌ Public  | Login & get tokens       |
| GET    | `/api/auth/me`       | ✅ Bearer  | Get current user info    |
| GET    | `/api/auth/health`   | ❌ Public  | Health check             |

---

## 🔧 Example Requests

### 1. Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "secret123",
  "fullName": "John Doe",
  "roles": ["user"]
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400000,
    "userId": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "roles": ["ROLE_USER"]
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

### 2. Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "secret123"
}
```
> Tip: `username` field accepts **username OR email**.

### 3. Access Protected Endpoint
```http
GET /api/auth/me
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## ⚙️ Configuration (`application.properties`)

| Property | Default | Description |
|---|---|---|
| `server.port` | `8080` | Server port |
| `app.jwt.secret` | `404E63...` | **Change in production!** 256-bit hex key |
| `app.jwt.expiration` | `86400000` | Access token TTL (24 hours in ms) |
| `app.jwt.refresh-expiration` | `604800000` | Refresh token TTL (7 days in ms) |

### Switch to MySQL
1. Add MySQL dependency in `pom.xml` (already commented in)
2. Uncomment MySQL properties in `application.properties`
3. Fill in your DB credentials

---

## 🗄️ Database Schema

```sql
CREATE TABLE users (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  UNIQUE NOT NULL,
    email      VARCHAR(100) UNIQUE NOT NULL,
    password   VARCHAR(255) NOT NULL,
    full_name  VARCHAR(100),
    enabled    BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role    VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

---

## 🔑 Roles

| Role | Description |
|---|---|
| `ROLE_USER` | Default role assigned on registration |
| `ROLE_ADMIN` | Admin privileges |
| `ROLE_MODERATOR` | Moderator privileges |

Protect endpoints by role using `@PreAuthorize`:
```java
@GetMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> adminOnly() { ... }
```

---

## 🛡️ Security Flow

```
Client → POST /api/auth/login
       ← 200 OK + { accessToken, refreshToken }

Client → GET /api/protected (Header: Authorization: Bearer <accessToken>)
       → JwtAuthenticationFilter validates token
       → Sets SecurityContext
       ← 200 OK + response
```

---

## 📦 Tech Stack

| Dependency | Version |
|---|---|
| Spring Boot | 3.2.0 |
| Spring Security | 6.x |
| JJWT | 0.12.3 |
| H2 Database | Runtime |
| Lombok | Latest |
| Java | 17 |

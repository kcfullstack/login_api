# User Authentication & OAuth2 Login Microservice

Production-ready, modular authentication microservice built using **Java 21 (LTS)**, **Spring Boot 3.3.x**, and **Spring Security 6.x** with Apache Maven.

---

## 🚀 Features

- **Modern Spring Security 6.x**: Declarative Lambda DSL configuration.
- **Federated OAuth2 Login**: Preconfigured for **Google** and **GitHub** social login.
- **OAuth2 Resource Server Support**: JWT Bearer token validation.
- **Dual Persistence Architecture**:
  - In-Memory **H2 Database** for rapid zero-configuration development and automated testing.
  - **MySQL** production profile via Spring Data JPA.
- **Automatic User Persistence**: Saves and updates federated OAuth2 user identities into the local database upon login.
- **SpringDoc OpenAPI 3.0 / Swagger UI**: Interactive API documentation and Bearer authentication testing.
- **Modular Multi-Package Architecture**: Clean separation of `config`, `controller`, `service`, `repository`, and `model`.

---

## 🛠️ Technology Stack

| Component | Technology | Version |
|---|---|---|
| Language | Java (LTS) | 21 |
| Framework | Spring Boot | 3.3.4 |
| Security | Spring Security | 6.3.x |
| Build Tool | Apache Maven | 3.9+ |
| Persistence | Spring Data JPA / Hibernate | 6.x |
| In-Memory DB | H2 Database | 2.x |
| Production DB | MySQL Connector/J | 8.x |
| API Docs | SpringDoc OpenAPI | 2.6.0 |
| Testing | JUnit 5, Mockito, Spring Security Test | Latest |

---

## 📁 Project Structure

```text
login_api/
├── pom.xml
├── README.md
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── wefour
│   │   │           └── utility
│   │   │               └── auth
│   │   │                   ├── AuthServiceApplication.java
│   │   │                   ├── config
│   │   │                   │   ├── OpenApiConfig.java
│   │   │                   │   ├── SecurityConfig.java
│   │   │                   │   └── WebClientConfig.java
│   │   │                   ├── controller
│   │   │                   │   ├── AuthController.java
│   │   │                   │   └── UserController.java
│   │   │                   ├── model
│   │   │                   │   ├── dto
│   │   │                   │   │   ├── ApiResponse.java
│   │   │                   │   │   ├── AuthStatusDto.java
│   │   │                   │   │   └── UserResponseDto.java
│   │   │                   │   └── entity
│   │   │                   │       ├── AuthProvider.java
│   │   │                   │       ├── Role.java
│   │   │                   │       └── User.java
│   │   │                   ├── repository
│   │   │                   │   └── UserRepository.java
│   │   │                   └── service
│   │   │                       ├── CustomOAuth2UserService.java
│   │   │                       ├── OAuth2UserInfoExtractor.java
│   │   │                       └── UserService.java
│   │   └── resources
│   │       ├── application.yml
│   │       └── application-mysql.yml
│   └── test
│       ├── java
│       │   └── com
│       │       └── wefour
│       │           └── utility
│       │               └── auth
│       │                   ├── AuthServiceApplicationTests.java
│       │                   └── controller
│       │                       └── UserControllerTest.java
│       └── resources
│           └── application-test.yml
```

---

## ⚙️ Configuration

### 1. OAuth2 Identity Provider Setup

Set your environment variables or replace placeholders in `src/main/resources/application.yml`:

#### Google Cloud Console Setup:
1. Navigate to [Google Cloud Console](https://console.cloud.google.com/apis/credentials).
2. Create an **OAuth 2.0 Client ID** (Application type: *Web application*).
3. Set Authorized Redirect URI:
   ```
   http://localhost:8080/login/oauth2/code/google
   ```
4. Export credentials:
   ```bash
   export GOOGLE_CLIENT_ID="your-client-id.apps.googleusercontent.com"
   export GOOGLE_CLIENT_SECRET="your-client-secret"
   ```

#### GitHub Developer Settings:
1. Go to **Settings** > **Developer settings** > **OAuth Apps** on GitHub.
2. Register a new OAuth application.
3. Set Authorization callback URL:
   ```
   http://localhost:8080/login/oauth2/code/github
   ```
4. Export credentials:
   ```bash
   export GITHUB_CLIENT_ID="your-github-client-id"
   export GITHUB_CLIENT_SECRET="your-github-client-secret"
   ```

---

## 📦 How to Build and Run

### 1. Run with In-Memory H2 (Default)

```bash
mvn clean spring-boot:run
```

### 2. Run with MySQL Profile

```bash
mvn clean spring-boot:run -Dspring-boot.run.profiles=mysql
```
Or export database variables:
```bash
export MYSQL_HOST=localhost
export MYSQL_PORT=3306
export MYSQL_DB=auth_db
export MYSQL_USER=root
export MYSQL_PASSWORD=yourpassword
mvn clean spring-boot:run -Dspring-boot.run.profiles=mysql
```

### 3. Package Executable JAR

```bash
mvn clean package -DskipTests
java -jar target/auth-service-1.0.0-SNAPSHOT.jar
```

### 4. Run Automated Tests

```bash
mvn clean test
```

---

## 🌐 Endpoints & API Reference

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/v1/auth/status` | Public | Service health and authentication status check |
| `GET` | `/api/v1/auth/providers` | Public | Available OAuth2 identity providers and login initiation links |
| `GET` | `/oauth2/authorization/google` | Public | Initiates Google OAuth2 authentication flow |
| `GET` | `/oauth2/authorization/github` | Public | Initiates GitHub OAuth2 authentication flow |
| `GET` | `/api/v1/auth/user` | Authenticated | Returns logged-in user profile, role, and OAuth2 claims |
| `GET` | `/user` | Authenticated | Alias for authenticated user profile |
| `GET` | `/api/v1/auth/logout` | Public/Auth | Logs out session, deletes `JSESSIONID`, clears security context |
| `GET` | `/swagger-ui.html` | Public | Interactive SpringDoc OpenAPI Swagger UI documentation |
| `GET` | `/v3/api-docs` | Public | OpenAPI 3 JSON specification |
| `GET` | `/h2-console` | Public | Embedded H2 database web console (`jdbc:h2:mem:authdb`) |

---

## 🧪 Testing with cURL

### Check Service Status (Public)
```bash
curl -X GET http://localhost:8080/api/v1/auth/status
```

### Response Example:
```json
{
  "success": true,
  "message": "Authentication service status checked.",
  "data": {
    "status": "UP",
    "authenticated": false,
    "principal": "anonymous",
    "authType": "NONE",
    "timestamp": "2026-10-01T01:30:00",
    "message": "Service is online. Authentication is required to access protected resources."
  },
  "timestamp": "2026-10-01T01:30:00"
}
```

### Fetch Logged-in User (Requires active OAuth2 session or Bearer token)
```bash
curl -X GET http://localhost:8080/api/v1/auth/user \
     -H "Cookie: JSESSIONID=<your-session-cookie>"
```
Or with Bearer JWT:
```bash
curl -X GET http://localhost:8080/api/v1/auth/user \
     -H "Authorization: Bearer <your-jwt-token>"
```

# 📦 Order Management System (Spring Boot + Angular)

A full-stack **Order Management System (OMS)** built using **Spring Boot** for the backend and **Angular** for the frontend.

---

## 📌 Description

This project is a complete Order Management System that enables users to manage **products, orders, and users** with **role-based access control**.  
It uses **JWT-based authentication** to secure REST APIs and provides a responsive Angular UI.

---

## ✨ Features

- 🔐 User authentication using JWT
- 🛡 Role-Based Access Control (Admin, User)
- 📦 Product management (Create, Read, Update, Delete)
- 🧾 Order creation and tracking
- 🔒 Secure REST APIs with Spring Security
- 📱 Responsive Angular UI

---

## 🛠 Tech Stack

### Backend
- Java 17
- Spring Boot
- Spring Security (JWT)
- JPA / Hibernate
- MySQL

### Frontend
- Angular
- TypeScript
- HTML, CSS

### Tools & DevOps
- Maven
- Docker
- Git & GitHub


---

## 🚀 Setup & Installation

### Prerequisites
- Java 17+
- Node.js
- Angular CLI
- MySQL

---

### 🔧 Backend Setup

git clone https://github.com/your-username/oms.git
cd backend
mvn clean install
mvn spring-boot:run

### 🎨 Frontend Setup
cd frontend
npm install
ng serve


### ⚙ Configuration

Update database and server configuration in:

backend/src/main/resources/application.properties
# Server
server.port=PORT_NUMBER

# Database
spring.datasource.url=DATABASE_URL
spring.datasource.username=USERNAME
spring.datasource.password=PASSWORD

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# JWT Configuration
app.jwt.secret=ChangeThisToA_LongRandomSecretKey_AtLeast_32_Chars
app.jwt.exp-minutes=120


### 🔐 Authentication & Authorization

JWT token-based authentication

Role-based access:

ADMIN: Full access

USER: Limited access


### Screnshots
# Login page:
<img width="1919" height="1009" alt="image" src="https://github.com/user-attachments/assets/979dc66c-8c77-47a7-90d9-8f92285b9f14" />

# Registration page:
<img width="1918" height="1015" alt="image" src="https://github.com/user-attachments/assets/525fd206-19f6-4902-9c2c-f8814c30e969" />

# Admin dashboard:
<img width="1919" height="1018" alt="image" src="https://github.com/user-attachments/assets/e2abb537-0a93-47dd-ab58-6e4cf348cb1c" />




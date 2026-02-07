# Order-Management-System-using-Springboot-and-Angular
A full-stack Order Management System built using Spring Boot and Angular.

## 📌 Description

This project is a full-stack Order Management System that allows users to manage products, orders, and users with role-based access control.  
It is built using Spring Boot for the backend and Angular for the frontend.

## ✨ Features

- User authentication with JWT
- Role-based access control (Admin, User)
- Product management (CRUD)
- Order creation and tracking
- Secure REST APIs
- Responsive Angular UI

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

### Tools
- Maven
- Docker
- Git & GitHub


## 📂 Project Structure

backend/
 ├── controller
 ├── service
 ├── repository
 ├── entity
 ├── dto
 └── security

frontend/
 ├── components
 ├── services
 └── modules


## 🚀 Setup & Installation

### Prerequisites
- Java 17+
- Node.js
- Angular CLI
- MySQL

### Backend Setup

git clone https://github.com/your-username/oms.git
cd backend
mvn clean install
mvn spring-boot:run

### Frontend Setup
cd frontend
npm install
ng serve

## ⚙ Configuration

Update database credentials in:

backend/src/main/resources/application.properties

Add this to application.properties file:
server.port=PORT_NUMBER

spring.datasource.url=DATABASE_URL
spring.datasource.username=USERNAME
spring.datasource.password=PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# JWT
app.jwt.secret=ChangeThisToA_LongRandomSecretKey_AtLeast_32_Chars
app.jwt.exp-minutes=120


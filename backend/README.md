# OMS Backend (Spring Boot + JWT) - com.wipro.oms

## Requirements
- Java 17
- Maven
- MySQL (or change datasource settings)

## Run
1) Create DB:
   CREATE DATABASE omsdb;
2) Update `src/main/resources/application.properties`
3) Run:
   mvn spring-boot:run

## Default seeded users
- admin / admin123 (ROLE_ADMIN)

## API
- POST /api/auth/login
- Admin Users: /api/admin/users (ADMIN only)
- Products: /api/products
- Orders: /api/orders
- Dashboard: /api/dashboard/stats

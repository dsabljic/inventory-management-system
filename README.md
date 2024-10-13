# Inventory Management System

[frontend](https://github.com/dsabljic/inventory-management-system-frontend)

This is a full-stack web application built with Spring Boot for the backend and React for the frontend, designed to manage the inventory of rentable items and desks across different rooms.

## Features:
- **User Authentication:** JWT-based authentication with the ability for users to sign up, sign in, and reset their password via email verification.
- **Inventory Management:** Users can browse and rent items or desks in specific rooms.
- **Admin Panel:** Admin users have full control over managing users, items, and rooms, with the ability to perform CRUD operations.
- **Email Verification:** Newly registered users are required to verify their email before accessing the system.
- **Database:** PostgreSQL is used for secure and scalable data storage, with Liquibase for database migrations.

### Technologies Used:
- **Backend:** Spring Boot, JWT for authentication, Liquibase for database migrations.
- **Frontend:** React.js
- **Database:** PostgreSQL

### Configuration:
- **Database URL:** `spring.datasource.url=jdbc:postgresql://localhost:5432/inventory`
  > Note: The database must already exist.

---

## Getting Started

1. **Generating SSL Certificate:**
   ```shell
   keytool -genkeypair -alias inventoryapi -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 3650
   ```

2. **Saving Keystore and Database Password:**

   `cd` into `src/main/resources` and run the following command
   ```shell
   echo -e "KEYSTORE_PASSWORD=your_keystore_password\nDB_PASSWORD=your_db_password" > .env
   ```
3. **Database:**

If you take a look at the `application.properties` file you'll see the following config:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/inventory
```

so you'll need to make sure that the database with the appropriate name exists and update the database credentials accordingly.

## ER diagram

![Screenshot from 2024-07-17 15-57-23](https://github.com/user-attachments/assets/e4f20af9-39de-4f3b-a7cb-3209604aa71c)

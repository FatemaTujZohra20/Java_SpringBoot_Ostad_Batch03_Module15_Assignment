This README is designed to guide a user through the Secure Note-Taking App, explaining how to register users, 
manage notes, and verify data using the H2 Console.

# Secure Note-Taking App
A Spring Boot 3 application featuring role-based access control (RBAC) with Spring Security 6. 
This application allows users to register, create personal notes, and allows admins to oversee all notes in the system.

## 🚀 Getting Started
### Prerequisites

- Java 21

- Maven

- Postman (for API testing)

- Database Access (H2 Console)

- The application uses an in-memory/file-based H2 database.

`URL: http://localhost:8080/h2-console`

- JDBC URL: 
`jdbc:h2:file:./data/secure_note`

- User: `sa`

- Password: (leave empty)

## 🛠 API Endpoints & Testing Flow

Follow these steps in Postman to test the full functionality of the application.

### 1. User Registration

   Before accessing notes, you must register accounts with specific roles.


| Action | Method | Endpoint | Body (JSON) |
| -------- | -------- | -------- | -------- |
| Register User | POST | /api/auth/register/user | {"username": "student", "password": "password123"} |
| Register Admin | POST | api/auth/register/admin | {"username": "professor", "password": "adminpassword"} |

`Note: 
If the username is already taken, as I have already set the username so the API will return: "Error: Username 
is already taken!".`


### 2. Note Management (Role: USER)
   Only users with the ROLE_USER can create and view their own notes. Use Basic Auth in Postman with the credentials created above.

#### **_Create a Note_**
___

Method: `POST`

Endpoint: `/api/notes`

Body:  `{"title": "My First Note", 
"content": "This is private content."}`

#### **_Retrieve Own Notes_**
___

Method: `GET`

Endpoint: `/api/notes`

### 3. Global Oversight (Role: ADMIN)
   Only users with the ROLE_ADMIN can access the oversight endpoint to see all notes created in the system.

#### **_Global Oversight_**
___

Method: `GET`

Endpoint: `/api/admin/notes`

Auth: `Basic Auth (Username: professor, Password: adminpassword)`

# 📊 Verifying Data in H2 Console
Once you have sent requests via Postman, you can verify the data persistence directly in the browser:

#### Navigate to http://localhost:8080/h2-console.

Connect using the JDBC URL: `jdbc:h2:file:./data/secure_note`.

### **_Run the following SQL queries:_**
___

- To see registered users and their encrypted passwords:

`SELECT * FROM USER_ENTITY;`

- To see all notes and the owner_username associated with them:

`SELECT * FROM NOTE;`


# 🔒 Security Configuration Notes

- CSRF: Disabled for testing purposes.

- Frame Options: Set to sameOrigin to allow the H2 Console to render correctly in the browser.

- `Authentication: Uses HTTP Basic Auth. Ensure selecting this in the Authorization tab
in Postman for all protected routes.`
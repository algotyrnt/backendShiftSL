# **ShiftSL - Backend**

### **Dynamic Scheduling System for Healthcare Shift Management**

The backend of ShiftSL is responsible for managing business logic, RESTful APIs, and data processing for the dynamic scheduling system. This system is tailored for healthcare institutions to optimize shift management, leave handling, and employee notifications.

---

## **Table of Contents**
1. [Project Description](#project-description)
2. [Features](#features)
3. [Tech Stack](#tech-stack)
4. [Setup Instructions](#setup-instructions)
5. [API Endpoints](#api-endpoints)
6. [Testing](#testing)
7. [Contributing](#contributing)
8. [License](#license)

---

## **Project Description**

**ShiftSL** aims to replace manual scheduling in the healthcare sector with a robust and automated system. The backend, built using Spring Boot, provides endpoints for scheduling shifts, managing leaves, staff reallocation, and generating reports for HR. It integrates seamlessly with the frontend (Angular) and mobile app (Flutter).

---

## **Features**

1. **Dynamic Shift Management**:
    - Automatically assign or reallocate shifts based on availability and preferences.

2. **Leave Management**:
    - Supports planned and emergency leave requests.

3. **Real-time Updates**:
    - Notify staff about shift changes and approvals using a notification system (e.g., WhatsApp API).

4. **Payroll Support**:
    - Calculate hours worked, overtime, and generate HR-friendly reports.

5. **API Security**:
    - Secure user authentication and role-based access.

6. **Data Storage**:
    - Real-time data storage and retrieval using **Firestore**.

---

## **Tech Stack**

- **Backend Framework**: Spring Boot
- **Programming Language**: Java
- **Database**: Firestore (NoSQL)
- **Authentication**: Firebase Authentication
- **API Documentation**: Swagger
- **Notification Integration**: WhatsApp API
- **Deployment**: Google Cloud Platform (GCP)

---

## **Setup Instructions**

Follow these steps to set up the backend server:

1. **Clone the Repository**
   ```bash  
   git clone https://github.com/your-username/shiftsl-backend.git  
   cd shiftsl-backend  
   ```  

2. **Install Dependencies**  
   Ensure you have **Java 21+** and **Maven** installed.
   ```bash  
   mvn install  
   ```  

3. **Configure Application Properties**  
   Update `src/main/resources/application.properties` with the following:
   ```properties  
   spring.datasource.url=YOUR_FIREBASE_DATABASE_URL  
   spring.datasource.username=YOUR_USERNAME  
   spring.datasource.password=YOUR_PASSWORD  
   firebase.api.key=YOUR_FIREBASE_API_KEY  
   whatsapp.api.url=YOUR_WHATSAPP_API_URL  
   ```

4. **Run the Application**  
   Start the Spring Boot server:
   ```bash  
   mvn spring-boot:run  
   ```  

5. **Access the Backend**
    - Default Port: `http://localhost:8080`
    - API Documentation (Swagger): `http://localhost:8080/swagger-ui.html`

---

## **API Endpoints**

| Method | Endpoint                     | Description                                |  
|--------|------------------------------|--------------------------------------------|  
| POST   | `/api/auth/register`         | Register a new user (Admin/Staff)          |  
| POST   | `/api/auth/login`            | User login and authentication              |  


Refer to **Swagger UI** for complete API documentation.

---

## **Testing**

Unit and integration tests are managed using **JUnit**.

1. **Run Tests**
   ```bash  
   mvn test  
   ```  

2. **Test Coverage**  
   Test results can be viewed in the `/target/site` directory.

---

## **Contributing**

Follow these steps to contribute:
1. Fork the repository.
2. Create a new branch: `git checkout -b feature-name`.
3. Commit your changes: `git commit -m "Add feature"`
4. Push to your branch: `git push origin feature-name`.
5. Submit a pull request.

---

## **License**

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## **Contact**

For any queries, contact the team:
- **Project Team**: ShiftSL
- **Email**: info@shiftsl.com


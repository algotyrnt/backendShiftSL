# ShiftSL - Backend

The backend of **ShiftSL** is responsible for managing business logic, RESTful APIs, and data processing for the dynamic scheduling system. This system is tailored for healthcare institutions to optimize shift management, leave handling, and employee notifications.

## Project Description
**ShiftSL** aims to replace manual scheduling in the healthcare sector with a robust and automated system. The backend, built using **Spring Boot**, provides endpoints for scheduling shifts, managing leaves, staff reallocation, and generating reports for HR. It integrates seamlessly with the frontend (Angular) and mobile app (Flutter).

## Features
- **Dynamic Shift Management**:
    - Automatically assign or reallocate shifts based on availability and preferences.
- **Leave Management**:
    - Supports planned and emergency leave requests.
- **Real-time Updates**:
    - Notify staff about shift changes and approvals using a notification system (e.g., WhatsApp API).

## Tech Stack
- **Backend Framework**: Spring Boot
- **Programming Language**: Java
- **Database**: MySQL
- **API Documentation**: Swagger (for easy exploration of the API)
- **Notification Integration**: WhatsApp API
- **Deployment**: Google Cloud Platform (GCP)

## API Documentation (Swagger)
You can access the **Swagger API Documentation** at the following locations:

- **Development/Local Environment**:  
  If you're running the backend locally, the Swagger documentation is available at:  
  `http://localhost:8080/api/dev/swagger-ui/index.html`

- **Production Environment**:  
  For the live version of the API, the Swagger documentation can be accessed via:  
  `****/api/dev/swagger-ui/index.html`  
  

The Swagger UI provides an interactive interface that allows you to explore the available API endpoints, view request/response models, and test them directly.

## Contributing
Follow these steps to contribute:

1. Fork the repository.
2. Create a new branch: `git checkout -b feature-name`.
3. Commit your changes: `git commit -m "Add feature"`.
4. Push to your branch: `git push origin feature-name`.
5. Submit a pull request.

## Contact
For any queries, contact the team:

- **Project Team**: ShiftSL
- **Email**: info@shiftsl.com

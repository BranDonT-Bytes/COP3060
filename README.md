# Campus Resource Manager - Full Stack Application

A complete React + Spring Boot application for managing campus resources, locations, and categories with a persistent MySQL database.

## 📋 Project Overview

This is a **full-stack campus resource management system** with:
- **Frontend**: React 19 with React Router for navigation and Axios for API calls
- **Backend**: Spring Boot 3 with Spring Data JPA for persistent MySQL storage
- **Database**: MySQL for reliable data persistence
- **REST API**: Comprehensive CRUD endpoints for three entities

### Features
✅ Dashboard with statistics  
✅ Data display with pagination  
✅ Create, Read, Update, Delete operations  
✅ Form validation (frontend + backend)  
✅ CORS enabled for local development  
✅ Error handling and user feedback  
✅ Responsive design  

---

## 🏗️ Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                   React Frontend (Port 3000)                │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  Home Page (Dashboard) → Data Display → Form Page   │   │
│  │  (Stats)               (Tables)        (Create)     │   │
│  └─────────────────────────────────────────────────────┘   │
│                           ↕ Axios HTTP                       │
└─────────────────────────────────────────────────────────────┘
                        ↕ REST API (CORS)
┌─────────────────────────────────────────────────────────────┐
│            Spring Boot Backend (Port 8080)                  │
│  ┌──────────────┬──────────────┬──────────────┐             │
│  │  Controller  │   Service    │  Repository  │ JPA         │
│  │  (HTTP)      │  (Business)  │  (Database)  │             │
│  └──────────────┴──────────────┴──────────────┘             │
│         ↕ Exception Handling & Validation                   │
└─────────────────────────────────────────────────────────────┘
                           ↕
          ┌─────────────────────────────────┐
          │   MySQL Database (campus_db)    │
          │  ┌────────┬────────┬──────────┐ │
          │  │ catego-│location│ resource │ │
          │  │ ries   │s       │s         │ │
          │  └────────┴────────┴──────────┘ │
          └─────────────────────────────────┘
```

---

## 🚀 Quick Start

### Prerequisites

- **Java 17+** ([Download JDK 17](https://www.oracle.com/java/technologies/downloads/#java17))
- **Maven 3.8+** ([Download](https://maven.apache.org/download.cgi))
- **Node.js 16+** & **npm** ([Download](https://nodejs.org))
- **MySQL 8.0+** ([Download](https://dev.mysql.com/downloads/mysql/))

### Step 1: Set up the Database

1. Open MySQL Command Line or MySQL Workbench
2. Create the database:
   ```sql
   CREATE DATABASE campus_db;
   ```
3. Verify:
   ```sql
   SHOW DATABASES;
   ```

### Step 2: Start the Backend

Open PowerShell in the repo root:

```powershell
cd .\cop3060-backend
mvn clean install
mvn spring-boot:run
```

**Expected output:**
```
... c.c.Application : Started Application in 3.5 seconds
```

✅ Backend is running at: **http://localhost:8080**

### Step 3: Start the Frontend

Open another PowerShell terminal:

```powershell
cd .\campus-resource-frontend
npm install
npm start
```

**Expected output:**
```
Compiled successfully!
On Your Network:  http://192.168.x.x:3000
```

✅ Frontend is running at: **http://localhost:3000**

---

## 📡 REST API Endpoints

### Categories

| Method | Endpoint | Purpose |
|--------|----------|---------|
| **GET** | `/api/categories` | List all categories (paginated) |
| **GET** | `/api/categories/{id}` | Get category by ID |
| **POST** | `/api/categories` | Create new category |
| **PUT** | `/api/categories/{id}` | Update existing category |
| **DELETE** | `/api/categories/{id}` | Delete category |

**Example POST Request:**
```json
POST /api/categories
Content-Type: application/json

{
  "name": "Electronics",
  "description": "Electronic devices and equipment"
}
```

### Locations

| Method | Endpoint | Purpose |
|--------|----------|---------|
| **GET** | `/api/locations` | List all locations (paginated) |
| **GET** | `/api/locations/{id}` | Get location by ID |
| **POST** | `/api/locations` | Create new location |
| **PUT** | `/api/locations/{id}` | Update existing location |
| **DELETE** | `/api/locations/{id}` | Delete location |

**Example POST Request:**
```json
POST /api/locations
Content-Type: application/json

{
  "building": "Science Hall",
  "room": "201"
}
```

### Resources

| Method | Endpoint | Purpose |
|--------|----------|---------|
| **GET** | `/api/resources` | List all resources (paginated) |
| **GET** | `/api/resources/{id}` | Get resource by ID |
| **GET** | `/api/resources?category={id}` | Filter by category |
| **GET** | `/api/resources?q={query}` | Search by name |
| **POST** | `/api/resources` | Create new resource |
| **PUT** | `/api/resources/{id}` | Update existing resource |
| **DELETE** | `/api/resources/{id}` | Delete resource |

**Example POST Request:**
```json
POST /api/resources
Content-Type: application/json

{
  "name": "Microscope",
  "description": "Optical microscope for lab use",
  "locationId": 1,
  "categoryId": 2
}
```

---

## 🧪 Testing the API

### Using cURL (PowerShell)

```powershell
# Test backend health - list all resources
curl http://localhost:8080/api/resources

# Create a category
$body = @{
    name = "Lab Equipment"
    description = "Scientific lab equipment"
} | ConvertTo-Json

curl -X POST `
  -H "Content-Type: application/json" `
  -Body $body `
  http://localhost:8080/api/categories

# Create a location
$body = @{
    building = "Physics Building"
    room = "102"
} | ConvertTo-Json

curl -X POST `
  -H "Content-Type: application/json" `
  -Body $body `
  http://localhost:8080/api/locations
```

### Using React Frontend

1. Navigate to **http://localhost:3000**
2. Go to **"Create"** tab
3. Create a category (e.g., "Electronics")
4. Create a location (e.g., "Science Hall - Room 201")
5. Create a resource using those IDs
6. View created data in **"View Data"** tab

---

## 📁 Project Structure

```
COP3060-main/
├── cop3060-backend/
│   ├── pom.xml                              # Maven dependencies
│   └── src/main/java/com/cop_3060/
│       ├── Application.java                 # Spring Boot entry point
│       ├── controller/                      # REST controllers
│       │   ├── CategoryController.java
│       │   ├── LocationController.java
│       │   └── ResourceController.java
│       ├── service/                         # Business logic
│       │   ├── CategoryService.java
│       │   ├── LocationService.java
│       │   └── ResourceService.java
│       ├── repository/                      # JPA repositories
│       │   ├── CategoryRepository.java
│       │   ├── LocationRepository.java
│       │   └── ResourceRepository.java
│       ├── entity/                          # JPA entities
│       │   ├── Category.java
│       │   ├── Location.java
│       │   └── Resource.java
│       ├── dto/                             # Data transfer objects
│       ├── exception/                       # Custom exceptions & handler
│       └── resources/
│           └── application.properties       # Database config
│
├── campus-resource-frontend/
│   ├── package.json                         # npm dependencies
│   ├── public/
│   │   └── index.html
│   └── src/
│       ├── App.js                           # Main app with routing
│       ├── App.css
│       ├── api.js                           # Axios client
│       ├── pages/
│       │   ├── Home.js                      # Dashboard
│       │   ├── Home.css
│       │   ├── DataDisplay.js               # View data
│       │   ├── DataDisplay.css
│       │   ├── FormPage.js                  # Create data
│       │   └── FormPage.css
│       └── index.js
│
└── README.md                                # This file
```

---

## ⚙️ Configuration

### MySQL Configuration

Edit `cop3060-backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/campus_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root  # Change if needed
spring.jpa.hibernate.ddl-auto=update  # auto-create/update tables
```

### Frontend API Base URL

Create `.env` in `campus-resource-frontend/`:

```
REACT_APP_API_URL=http://localhost:8080/api
```

---

## 🔒 Error Handling

All API errors return a consistent JSON format:

```json
{
  "timestamp": "2024-12-02T10:30:45.123456Z",
  "status": 404,
  "error": "Not Found",
  "message": "Resource 999 not found",
  "path": "/api/resources/999"
}
```

Common HTTP Status Codes:
- `200 OK` - Success
- `201 Created` - Resource created
- `204 No Content` - Deleted successfully
- `400 Bad Request` - Validation error
- `404 Not Found` - Resource doesn't exist
- `409 Conflict` - Can't delete (in use by other resources)
- `500 Internal Server Error` - Server error

---

## 🐛 Troubleshooting

### Backend won't start
- Ensure MySQL is running: `mysql --version`
- Check database exists: `CREATE DATABASE campus_db;`
- Clear Maven cache: `mvn clean` then `mvn install`

### Frontend can't connect to backend
- Check backend is running: `curl http://localhost:8080/api/resources`
- Verify CORS is enabled (check Application.java)
- Check `.env` for correct `REACT_APP_API_URL`

### Port already in use
```powershell
# Find process on port 8080
netstat -ano | findstr :8080

# Kill process (replace PID)
taskkill /PID <PID> /F
```

### Database error
```sql
-- Reset database
DROP DATABASE campus_db;
CREATE DATABASE campus_db;
```

---

## 📝 AI Usage Log

**Assistant: GitHub Copilot**

**Summary of Work:**
1. **Backend Setup**: Created Maven `pom.xml` with Spring Boot 3.2, Spring Data JPA, and MySQL dependencies
2. **Application Scaffolding**: Generated Spring Boot `Application.java` with CORS configuration for local development
3. **Entity Layer**: Created JPA entities (`Category`, `Location`, `Resource`) with proper relationships and validation
4. **Repository Layer**: Built `JpaRepository` interfaces with custom query methods for filtering and pagination
5. **Service Layer**: Implemented business logic with exception handling, DTO conversions, and database transactions
6. **Controller Layer**: Created REST controllers with full CRUD endpoints and proper HTTP status codes
7. **Database Config**: Set up `application.properties` for MySQL connection with auto table creation
8. **Frontend Setup**: Added React Router, Axios, and Lucide icons; created responsive pages
9. **Pages Implementation**:
   - Home/Dashboard: Shows statistics and quick links
   - Data Display: Tabbed interface for viewing categories, locations, and resources with pagination
   - Form Page: Multi-form component for creating categories, locations, and resources
10. **API Integration**: Built Axios client utility with CRUD methods for all three entities
11. **Styling**: Created consistent, responsive CSS with mobile-first design approach
12. **Documentation**: Wrote comprehensive README with architecture, endpoints, setup, and troubleshooting

**Technologies Used:**
- Backend: Spring Boot 3.2, Spring Data JPA, MySQL 8, Jakarta Validation, Maven
- Frontend: React 19, React Router 6, Axios, Lucide React Icons
- Database: MySQL with auto DDL
- Build Tools: Maven, npm, Webpack (via react-scripts)

**Key Features Implemented:**
✅ Complete REST API with CRUD operations  
✅ MySQL persistence with JPA relationships  
✅ Frontend-backend integration via Axios  
✅ React Router for multi-page navigation  
✅ Form validation (both frontend and backend)  
✅ CORS support for local development  
✅ Pagination and filtering capabilities  
✅ Exception handling with custom error responses  
✅ Responsive design for desktop and mobile  
✅ Professional styling and UX  

---

## 📄 License

This is an academic project for COP 3060 at FAMU.

---

## 🤝 Contributing

For suggestions or improvements, please submit an issue or pull request.

---

## 📧 Support

For questions or issues, refer to the troubleshooting section or check the browser console and server logs for detailed error messages.
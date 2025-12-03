## 🚀 QUICK RUN GUIDE

This file has the simplest, fastest steps to get your app running locally.

---

## ⚡ 3-Step Startup (Total: ~5 minutes)

### 1️⃣ Set Up Database (1 minute)

Open any MySQL client (Command Line, Workbench, etc.) and run:

```sql
CREATE DATABASE campus_db;
```

That's it! The tables will auto-create when the backend starts.

---

### 2️⃣ Start Backend (2 minutes)

Open **PowerShell** and navigate to the repo:

```powershell
cd C:\Users\BranDon\Desktop\COP3060-main
cd .\cop3060-backend

mvn clean install
mvn spring-boot:run
```

Wait for this line to appear:
```
... Started Application in X.X seconds
```

✅ **Backend is ready at: http://localhost:8080**

---

### 3️⃣ Start Frontend (2 minutes)

Open **another PowerShell window** and:

```powershell
cd C:\Users\BranDon\Desktop\COP3060-main
cd .\campus-resource-frontend

npm install
npm start
```

The browser will auto-open to: **http://localhost:3000**

✅ **Frontend is ready!**

---

## 🧪 Quick Test

1. **Go to Home page** → See statistics (should say 0 items)
2. **Go to Create tab** → Make a category:
   - Name: `Electronics`
   - Description: `Electronic equipment`
   - Click **Create** → Success message!
3. **Create a location:**
   - Building: `Science Hall`
   - Room: `201`
   - Click **Create** → Success!
4. **Create a resource:**
   - Name: `Microscope`
   - Description: `Lab microscope`
   - Select location and category you just created
   - Click **Create** → Success!
5. **Go to View Data tab** → See your 1 resource in the table ✅

---

## 🐛 If Something Goes Wrong

| Issue | Fix |
|-------|-----|
| **Backend won't start** | Make sure MySQL is running and database exists |
| **Frontend can't connect** | Check http://localhost:8080/api/resources works in browser |
| **Port already in use** | Kill process: `netstat -ano \| findstr :8080` then `taskkill /PID <PID> /F` |
| **npm install errors** | Delete `node_modules/` and `package-lock.json`, run `npm install` again |
| **Maven errors** | Run `mvn clean` first, then `mvn install` |

---

## 📚 Full Documentation

See **README.md** for:
- Architecture diagram
- API endpoint reference
- Configuration options
- Troubleshooting guide
- Project structure

---

## ✨ What You Just Built

A **full-stack campus resource manager** with:
- ✅ React frontend with 3 pages (Home, View Data, Create)
- ✅ Spring Boot backend with REST API
- ✅ MySQL database with automatic schema creation
- ✅ CORS enabled for development
- ✅ Form validation (both frontend & backend)
- ✅ Responsive design
- ✅ Professional error handling

**Total API endpoints: 15 CRUD operations** across 3 resources.

Enjoy! 🎉

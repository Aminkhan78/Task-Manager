# Team Task Manager

A modern full-stack Team Task Manager built for company selection assignment.

**Repository:** [github.com/Aminkhan78/Task-Manager](https://github.com/Aminkhan78/Task-Manager)

## Stack

- Backend: Java 17, Spring Boot, Spring Security (JWT), Spring Data MongoDB
- Database: MongoDB
- Frontend: React (Vite), HTML/CSS/JS
- APIs: REST API with validation, relationships, and RBAC

## Features

- Authentication: signup/login with JWT
- Role-based access: `ADMIN` and `MEMBER`
- Project management: create/list projects, add team members
- Task management: create tasks, assign users, update status
- Dashboard: total tasks, todo, in progress, done, overdue
- Modern UI: glassmorphism style with background image

## Run locally (manual)

### Prerequisites

- Java 17+ and Maven
- Node.js 18+
- MongoDB running locally (Windows service or Docker)

Default database URL: `mongodb://localhost:27017/teamtaskmanager`

### Option A — One script (Windows)

```powershell
cd scripts
.\start-local.ps1
```

This opens two terminals (backend + frontend). Open **http://localhost:5173**.

### Option B — Two terminals

**Terminal 1 — Backend**

```powershell
cd backend
mvn spring-boot:run
```

Wait until you see `Started TeamTaskManagerApplication`.

**Terminal 2 — Frontend**

```powershell
cd frontend
copy .env.example .env
npm install
npm run dev
```

Open **http://localhost:5173**. Backend API: **http://localhost:8080** (health: `/api/health`).

### Optional environment variables

| Variable | Default |
|----------|---------|
| `SPRING_DATA_MONGODB_URI` | `mongodb://localhost:27017/teamtaskmanager` |
| `APP_JWT_SECRET` | (dev default in `application.properties`) |
| `APP_JWT_EXPIRATION_MS` | `86400000` |
| `VITE_API_URL` | `http://localhost:8080` (in `frontend/.env`) |

### First use

1. Sign up at http://localhost:5173
2. Choose **Admin** to create projects and tasks
3. User/project IDs are MongoDB strings (not numbers)

---

## Push to GitHub

One-time login (if not already):

```powershell
gh auth login
```

Push latest code:

```powershell
cd a:\MinorProject\Team-task-manager
git push origin main
```

---

## Deploy free (Render + MongoDB Atlas)

### 1) MongoDB Atlas (free)

1. Create account at [mongodb.com/atlas](https://www.mongodb.com/atlas)
2. Create a free **M0** cluster
3. Database Access → add user + password
4. Network Access → allow `0.0.0.0/0` (for Render)
5. Connect → copy connection string, e.g.  
   `mongodb+srv://USER:PASSWORD@cluster.mongodb.net/teamtaskmanager`

### 2) Render (free)

1. Sign in at [render.com](https://render.com) with GitHub
2. **New** → **Blueprint** → connect repo `Aminkhan78/Task-Manager`
3. When prompted for `SPRING_DATA_MONGODB_URI`, paste your Atlas URI
4. Deploy — Render creates:
   - `task-manager-api` (Spring Boot Docker)
   - `task-manager-web` (static React site)

### 3) Live URLs

After deploy (about 5–10 minutes):

- **Frontend:** `https://task-manager-web.onrender.com` (name may vary in dashboard)
- **Backend:** `https://task-manager-api.onrender.com`

Test backend: `https://task-manager-api.onrender.com/api/health` → `{"status":"ok"}`

> Free Render services sleep after inactivity; first load may take ~30 seconds.

---

## API Endpoints

- `POST /api/auth/signup`
- `POST /api/auth/login`
- `GET /api/projects`
- `POST /api/projects` (ADMIN)
- `GET /api/users` (ADMIN)
- `POST /api/tasks` (ADMIN)
- `GET /api/tasks/project/{projectId}`
- `PATCH /api/tasks/{taskId}/status`
- `GET /api/tasks/dashboard`

## Assignment Submission Checklist

- [ ] Live URL (frontend on Render)
- [ ] GitHub repository URL
- [ ] Updated README
- [ ] 2-5 minute demo video

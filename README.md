# Team Task Manager

A modern full-stack Team Task Manager built for company selection assignment.

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

## Local Setup

### 1) MongoDB

Run MongoDB locally (default connection: `mongodb://localhost:27017/teamtaskmanager`):

```bash
docker run -d --name ttm-mongo -p 27017:27017 mongo:7
```

### 2) Backend

```bash
cd backend
mvn spring-boot:run
```

Set env vars (optional if using defaults):

- `SPRING_DATA_MONGODB_URI`
- `APP_JWT_SECRET` (must be a long secure key in production)
- `APP_JWT_EXPIRATION_MS`

### 3) Frontend

```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

Frontend runs on `http://localhost:5173`, backend on `http://localhost:8080`.

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

## Railway Deployment

Deploy backend and frontend as separate Railway services from this same repo.

### Backend service

- Root directory: `backend`
- Build: Dockerfile
- Required variables:
  - `SPRING_DATA_MONGODB_URI`
  - `APP_JWT_SECRET`
  - `APP_JWT_EXPIRATION_MS` (optional)

### Frontend service

- Root directory: `frontend`
- Build: Dockerfile
- Required variable:
  - `VITE_API_URL` = backend public URL

## Assignment Submission Checklist

- [ ] Live URL (frontend)
- [ ] GitHub repository URL
- [ ] Updated README
- [ ] 2-5 minute demo video

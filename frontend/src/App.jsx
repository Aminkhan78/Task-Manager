import { useCallback, useEffect, useMemo, useState } from "react";
import { apiRequest } from "./api";

const emptyLogin = { email: "", password: "" };
const emptySignup = { fullName: "", email: "", password: "", role: "MEMBER" };
const emptyProject = { name: "", description: "", memberIds: "" };
const emptyTask = { title: "", description: "", projectId: "", assigneeId: "", dueDate: "", status: "TODO" };

function App() {
  const [authMode, setAuthMode] = useState("login");
  const [loginForm, setLoginForm] = useState(emptyLogin);
  const [signupForm, setSignupForm] = useState(emptySignup);
  const [projectForm, setProjectForm] = useState(emptyProject);
  const [taskForm, setTaskForm] = useState(emptyTask);
  const [auth, setAuth] = useState(() => JSON.parse(localStorage.getItem("ttm_auth") || "null"));
  const [projects, setProjects] = useState([]);
  const [users, setUsers] = useState([]);
  const [tasksByProject, setTasksByProject] = useState({});
  const [dashboard, setDashboard] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const token = auth?.token;
  const role = auth?.role;

  const authHeader = useMemo(() => (token ? { Authorization: `Bearer ${token}` } : {}), [token]);

  const refreshData = useCallback(async () => {
    try {
      setLoading(true);
      setError("");
      const [projectList, dashboardStats] = await Promise.all([
        apiRequest("/api/projects", { headers: authHeader }),
        apiRequest("/api/tasks/dashboard", { headers: authHeader }),
      ]);
      setProjects(projectList);
      setDashboard(dashboardStats);
      if (role === "ADMIN") {
        const members = await apiRequest("/api/users", { headers: authHeader });
        setUsers(members);
      }
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }, [authHeader, role]);

  useEffect(() => {
    if (!token) return;
    // eslint-disable-next-line react-hooks/set-state-in-effect
    refreshData();
  }, [token, refreshData]);

  async function handleLogin(e) {
    e.preventDefault();
    setError("");
    try {
      const response = await apiRequest("/api/auth/login", { method: "POST", body: loginForm });
      localStorage.setItem("ttm_auth", JSON.stringify(response));
      setAuth(response);
      setLoginForm(emptyLogin);
    } catch (err) {
      setError(err.message);
    }
  }

  async function handleSignup(e) {
    e.preventDefault();
    setError("");
    try {
      const response = await apiRequest("/api/auth/signup", { method: "POST", body: signupForm });
      localStorage.setItem("ttm_auth", JSON.stringify(response));
      setAuth(response);
      setSignupForm(emptySignup);
    } catch (err) {
      setError(err.message);
    }
  }

  function logout() {
    localStorage.removeItem("ttm_auth");
    setAuth(null);
    setProjects([]);
    setTasksByProject({});
    setDashboard(null);
  }

  async function createProject(e) {
    e.preventDefault();
    const memberIds = projectForm.memberIds
      .split(",")
      .map((v) => v.trim())
      .filter(Boolean);
    try {
      await apiRequest("/api/projects", {
        method: "POST",
        headers: authHeader,
        body: { ...projectForm, memberIds },
      });
      setProjectForm(emptyProject);
      refreshData();
    } catch (err) {
      setError(err.message);
    }
  }

  async function createTask(e) {
    e.preventDefault();
    try {
      await apiRequest("/api/tasks", {
        method: "POST",
        headers: authHeader,
        body: {
          ...taskForm,
          projectId: taskForm.projectId,
          assigneeId: taskForm.assigneeId || null,
        },
      });
      setTaskForm(emptyTask);
      refreshData();
    } catch (err) {
      setError(err.message);
    }
  }

  async function loadTasks(projectId) {
    try {
      const tasks = await apiRequest(`/api/tasks/project/${projectId}`, { headers: authHeader });
      setTasksByProject((old) => ({ ...old, [projectId]: tasks }));
    } catch (err) {
      setError(err.message);
    }
  }

  async function updateTaskStatus(taskId, status) {
    try {
      await apiRequest(`/api/tasks/${taskId}/status`, {
        method: "PATCH",
        headers: authHeader,
        body: { status },
      });
      for (const project of projects) {
        if (tasksByProject[project.id]) {
          loadTasks(project.id);
        }
      }
      refreshData();
    } catch (err) {
      setError(err.message);
    }
  }

  if (!auth) {
    return (
      <div className="page auth-page">
        <div className="auth-card glass">
          <h1>Team Task Manager</h1>
          <p>Modern workspace to manage projects, assign tasks and track progress.</p>
          <div className="tabs">
            <button onClick={() => setAuthMode("login")} className={authMode === "login" ? "active" : ""}>Login</button>
            <button onClick={() => setAuthMode("signup")} className={authMode === "signup" ? "active" : ""}>Signup</button>
          </div>
          {authMode === "login" ? (
            <form onSubmit={handleLogin} className="form-grid">
              <input placeholder="Email" type="email" value={loginForm.email} onChange={(e) => setLoginForm({ ...loginForm, email: e.target.value })} required />
              <input placeholder="Password" type="password" value={loginForm.password} onChange={(e) => setLoginForm({ ...loginForm, password: e.target.value })} required />
              <button type="submit">Login</button>
            </form>
          ) : (
            <form onSubmit={handleSignup} className="form-grid">
              <input placeholder="Full Name" value={signupForm.fullName} onChange={(e) => setSignupForm({ ...signupForm, fullName: e.target.value })} required />
              <input placeholder="Email" type="email" value={signupForm.email} onChange={(e) => setSignupForm({ ...signupForm, email: e.target.value })} required />
              <input placeholder="Password" type="password" value={signupForm.password} onChange={(e) => setSignupForm({ ...signupForm, password: e.target.value })} required />
              <select value={signupForm.role} onChange={(e) => setSignupForm({ ...signupForm, role: e.target.value })}>
                <option value="MEMBER">Member</option>
                <option value="ADMIN">Admin</option>
              </select>
              <button type="submit">Create Account</button>
            </form>
          )}
          {error && <p className="error">{error}</p>}
        </div>
      </div>
    );
  }

  return (
    <div className="page app-page">
      <header className="glass header">
        <h2>Team Task Manager</h2>
        <div>
          <span>{auth.fullName} ({auth.role})</span>
          <button onClick={logout}>Logout</button>
        </div>
      </header>

      {error && <p className="error">{error}</p>}
      {loading && <p className="hint">Refreshing workspace...</p>}

      {dashboard && (
        <section className="stats-grid">
          <article className="glass stat"><h3>Total</h3><p>{dashboard.totalTasks}</p></article>
          <article className="glass stat"><h3>Todo</h3><p>{dashboard.todoTasks}</p></article>
          <article className="glass stat"><h3>In Progress</h3><p>{dashboard.inProgressTasks}</p></article>
          <article className="glass stat"><h3>Done</h3><p>{dashboard.doneTasks}</p></article>
          <article className="glass stat"><h3>Overdue</h3><p>{dashboard.overdueTasks}</p></article>
        </section>
      )}

      {role === "ADMIN" && (
        <section className="forms">
          <form className="glass form-grid" onSubmit={createProject}>
            <h3>Create Project</h3>
            <input placeholder="Project Name" value={projectForm.name} onChange={(e) => setProjectForm({ ...projectForm, name: e.target.value })} required />
            <textarea placeholder="Description" value={projectForm.description} onChange={(e) => setProjectForm({ ...projectForm, description: e.target.value })} />
            <input placeholder="Member IDs (comma separated)" value={projectForm.memberIds} onChange={(e) => setProjectForm({ ...projectForm, memberIds: e.target.value })} />
            <button type="submit">Create Project</button>
          </form>

          <form className="glass form-grid" onSubmit={createTask}>
            <h3>Create Task</h3>
            <input placeholder="Task Title" value={taskForm.title} onChange={(e) => setTaskForm({ ...taskForm, title: e.target.value })} required />
            <textarea placeholder="Description" value={taskForm.description} onChange={(e) => setTaskForm({ ...taskForm, description: e.target.value })} />
            <select value={taskForm.projectId} onChange={(e) => setTaskForm({ ...taskForm, projectId: e.target.value })} required>
              <option value="">Select Project</option>
              {projects.map((project) => <option key={project.id} value={project.id}>{project.name}</option>)}
            </select>
            <select value={taskForm.assigneeId} onChange={(e) => setTaskForm({ ...taskForm, assigneeId: e.target.value })}>
              <option value="">Assign to user</option>
              {users.map((user) => <option key={user.id} value={user.id}>{user.fullName}</option>)}
            </select>
            <input type="date" value={taskForm.dueDate} onChange={(e) => setTaskForm({ ...taskForm, dueDate: e.target.value })} />
            <button type="submit">Create Task</button>
          </form>
        </section>
      )}

      <section className="glass project-list">
        <h3>Projects</h3>
        {projects.length === 0 && <p>No projects yet.</p>}
        {projects.map((project) => (
          <article key={project.id} className="project-card">
            <div className="project-top">
              <div>
                <h4>{project.name}</h4>
                <p>{project.description || "No description"}</p>
              </div>
              <button onClick={() => loadTasks(project.id)}>Load Tasks</button>
            </div>
            <p className="members">Members: {project.members?.map((m) => m.fullName).join(", ")}</p>

            {(tasksByProject[project.id] || []).map((task) => (
              <div key={task.id} className="task-row">
                <div>
                  <strong>{task.title}</strong>
                  <p>{task.description || "No description"}</p>
                  <small>Assignee: {task.assigneeName || "Unassigned"} | Due: {task.dueDate || "N/A"}</small>
                </div>
                <select value={task.status} onChange={(e) => updateTaskStatus(task.id, e.target.value)}>
                  <option value="TODO">TODO</option>
                  <option value="IN_PROGRESS">IN PROGRESS</option>
                  <option value="DONE">DONE</option>
                </select>
              </div>
            ))}
          </article>
        ))}
      </section>
    </div>
  );
}

export default App;

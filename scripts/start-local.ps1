# Start Team Task Manager locally (Windows)
$ErrorActionPreference = "Stop"
$root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)

Write-Host "Checking MongoDB service..."
$mongo = Get-Service -Name "MongoDB" -ErrorAction SilentlyContinue
if ($mongo -and $mongo.Status -ne "Running") {
    Start-Service MongoDB
    Write-Host "Started MongoDB service."
} elseif ($mongo) {
    Write-Host "MongoDB is running."
} else {
    Write-Host "MongoDB service not found. Ensure MongoDB is installed and running on mongodb://localhost:27017"
}

$backendDir = Join-Path $root "backend"
$frontendDir = Join-Path $root "frontend"

if (-not (Test-Path (Join-Path $frontendDir ".env"))) {
    Copy-Item (Join-Path $frontendDir ".env.example") (Join-Path $frontendDir ".env")
    Write-Host "Created frontend/.env from .env.example"
}

Write-Host "Starting backend on http://localhost:8080 ..."
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backendDir'; mvn spring-boot:run"

Start-Sleep -Seconds 3

Write-Host "Starting frontend on http://localhost:5173 ..."
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$frontendDir'; if (-not (Test-Path node_modules)) { npm install }; npm run dev"

Write-Host ""
Write-Host "Open http://localhost:5173 in your browser when both windows show ready."

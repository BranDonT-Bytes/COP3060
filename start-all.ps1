# start-all.ps1 - build frontend, copy build into backend static, build backend, run jar
# Run from repository root. Requires PowerShell, Node (npm), Maven and Java on PATH.

$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $MyInvocation.MyCommand.Definition
Write-Host "Repository root: $root"

# Build frontend
Push-Location (Join-Path $root 'campus-resource-frontend')
Write-Host "Installing frontend dependencies..."
npm install
Write-Host "Building frontend..."
npm run build
Pop-Location

# Copy build to backend static resources
$buildDir = Join-Path $root 'campus-resource-frontend\build'
$staticDir = Join-Path $root 'cop3060-backend\src\main\resources\static'
Write-Host "Preparing backend static folder: $staticDir"
if (Test-Path $staticDir) { Remove-Item -Recurse -Force $staticDir }
New-Item -ItemType Directory -Path $staticDir | Out-Null
Write-Host "Copying build files to backend static folder..."
robocopy $buildDir $staticDir /E /NFL /NDL /NJH /NJS /nc /ns /np | Out-Null

# Build backend
Push-Location (Join-Path $root 'cop3060-backend')
Write-Host "Building backend (Maven)..."
mvn -U clean package -DskipTests
Pop-Location

# Kill existing process on 8080 (if any)
$port = 8080
try {
    $conn = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($conn) {
        $pid = $conn.OwningProcess
        if ($pid) {
            Write-Host "Stopping existing process PID $pid listening on port $port..."
            Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
            Start-Sleep -Seconds 1
        }
    }
} catch {
    Write-Host "Warning: could not query port $port (continuing)"
}

# Start backend jar
$jar = Join-Path $root 'cop3060-backend\target\cop3060-backend-0.0.1-SNAPSHOT.jar'
if (-not (Test-Path $jar)) {
    Write-Error "Jar not found at $jar. Build may have failed."
    exit 1
}
Write-Host "Starting backend jar: $jar"
$proc = Start-Process -FilePath 'java' -ArgumentList '-jar', $jar -WorkingDirectory (Join-Path $root 'cop3060-backend') -PassThru
Write-Host "Backend started with PID $($proc.Id). Give it a few seconds to initialize."

Write-Host "Done. Visit http://localhost:8080/ to view the app or call API endpoints under /api/."

$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $MyInvocation.MyCommand.Definition
Write-Host "Repository root: $root"

# Build frontend
Push-Location (Join-Path $root 'campus-resource-frontend')
Write-Host "Installing frontend dependencies..."
npm install
Write-Host "Building frontend..."
npm run build
Pop-Location

# Copy build to backend static resources
$buildDir = Join-Path $root 'campus-resource-frontend\build'
$staticDir = Join-Path $root 'cop3060-backend\src\main\resources\static'
Write-Host "Preparing backend static folder: $staticDir"
if (Test-Path $staticDir) { Remove-Item -Recurse -Force $staticDir }
New-Item -ItemType Directory -Path $staticDir | Out-Null
Write-Host "Copying build files to backend static folder..."
robocopy $buildDir $staticDir /E /NFL /NDL /NJH /NJS /nc /ns /np | Out-Null

# Ensure any running backend using port 8080 or the target jar is stopped before rebuilding
$port = 8080
Write-Host "Checking for processes using port $port..."
try {
    $conn = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($conn) {
        $pid = $conn.OwningProcess
        if ($pid) {
            Write-Host "Stopping existing process PID $pid listening on port $port..."
            Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
            Start-Sleep -Seconds 1
        }
    }
} catch {
    Write-Host "Warning: could not query port $port (continuing)"
}

# Additionally, attempt to stop java processes that are running the backend jar
$jarRelative = 'cop3060-backend\target\cop3060-backend-0.0.1-SNAPSHOT.jar'
$jarFull = Join-Path $root $jarRelative
Write-Host "Looking for java processes referencing $jarFull ..."
try {
    $procs = Get-CimInstance Win32_Process -Filter "Name = 'java.exe'" | Where-Object { $_.CommandLine -and $_.CommandLine -like "*cop3060-backend*jar*" }
    foreach ($p in $procs) {
        Write-Host "Stopping java process PID $($p.ProcessId) with command: $($p.CommandLine)"
        Stop-Process -Id $p.ProcessId -Force -ErrorAction SilentlyContinue
        Start-Sleep -Seconds 1
    }
} catch {
    Write-Host "Warning: failed to enumerate java processes: $_"
}

# Build backend with retries for cleaning target (handle file locks)
Push-Location (Join-Path $root 'cop3060-backend')
$maxRetries = 5
$attempt = 0
while ($attempt -lt $maxRetries) {
    $attempt++
    Write-Host "Attempt ${attempt}: Cleaning and building backend..."
    try {
        mvn -U clean package -DskipTests
        Write-Host "Backend build succeeded."
        break
    } catch {
    Write-Host "Build failed on attempt ${attempt}: $_"
        Write-Host "Retrying in 2 seconds..."
        Start-Sleep -Seconds 2
        continue
    }
}
if ($attempt -ge $maxRetries) {
    Write-Error "Backend build failed after $maxRetries attempts. Aborting."
    Pop-Location
    exit 1
}
Pop-Location

# Start backend jar
$jar = Join-Path $root 'cop3060-backend\target\cop3060-backend-0.0.1-SNAPSHOT.jar'
if (-not (Test-Path $jar)) {
    Write-Error "Jar not found at $jar. Build may have failed."
    exit 1
}
Write-Host "Starting backend jar: $jar"
$proc = Start-Process -FilePath 'java' -ArgumentList '-jar', $jar -WorkingDirectory (Join-Path $root 'cop3060-backend') -PassThru
Write-Host "Backend started with PID $($proc.Id). Give it a few seconds to initialize."

Write-Host "Done. Visit http://localhost:8080/ to view the app or call API endpoints under /api/."

# End

# ============================================================
# UniShield - Dependency Setup Script
# Downloads JavaFX SDK and MySQL Connector
# ============================================================

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  UniShield - Dependency Setup" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$libDir = "lib"
if (!(Test-Path $libDir)) { New-Item -ItemType Directory -Path $libDir | Out-Null }

# --- Download JavaFX SDK 23.0.1 for Windows ---
$javafxZip = "$libDir\openjfx-23.0.1_windows-x64_bin-sdk.zip"
$javafxDir = "$libDir\javafx-sdk-23.0.1"

if (!(Test-Path $javafxDir)) {
    Write-Host "`n[1/2] Downloading JavaFX SDK 23.0.1..." -ForegroundColor Yellow
    $javafxUrl = "https://download2.gluonhq.com/openjfx/23.0.1/openjfx-23.0.1_windows-x64_bin-sdk.zip"
    try {
        Invoke-WebRequest -Uri $javafxUrl -OutFile $javafxZip -UseBasicParsing
        Write-Host "  Extracting JavaFX SDK..." -ForegroundColor Yellow
        Expand-Archive -Path $javafxZip -DestinationPath $libDir -Force
        Remove-Item $javafxZip -Force
        Write-Host "  JavaFX SDK ready!" -ForegroundColor Green
    } catch {
        Write-Host "  ERROR: Failed to download JavaFX SDK." -ForegroundColor Red
        Write-Host "  Please download manually from: https://gluonhq.com/products/javafx/" -ForegroundColor Red
        Write-Host "  Extract to: $javafxDir" -ForegroundColor Red
    }
} else {
    Write-Host "`n[1/2] JavaFX SDK already present." -ForegroundColor Green
}

# --- Download MySQL Connector/J ---
$mysqlJar = "$libDir\mysql-connector-j-8.3.0.jar"

if (!(Test-Path $mysqlJar)) {
    Write-Host "`n[2/2] Downloading MySQL Connector/J 8.3.0..." -ForegroundColor Yellow
    $mysqlUrl = "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.3.0/mysql-connector-j-8.3.0.jar"
    try {
        Invoke-WebRequest -Uri $mysqlUrl -OutFile $mysqlJar -UseBasicParsing
        Write-Host "  MySQL Connector ready!" -ForegroundColor Green
    } catch {
        Write-Host "  ERROR: Failed to download MySQL Connector." -ForegroundColor Red
        Write-Host "  Please download manually from Maven Central." -ForegroundColor Red
    }
} else {
    Write-Host "`n[2/2] MySQL Connector already present." -ForegroundColor Green
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  Setup Complete!" -ForegroundColor Green
Write-Host "  Next: Run build_and_run.bat" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

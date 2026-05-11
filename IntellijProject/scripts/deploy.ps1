$ErrorActionPreference = "Stop"

function Write-Log {
    param(
        [string]$Message
    )

    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    Write-Host "[$timestamp] $Message"
}

function Ensure-Success {
    param(
        [string]$Step
    )

    if ($LASTEXITCODE -ne 0) {
        throw "$Step failed with exit code $LASTEXITCODE"
    }
}

# 이 스크립트 파일이 있는 scripts 폴더를 기준으로 프로젝트 루트를 계산한다.
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = Resolve-Path (Join-Path $scriptDir "..")
$composeDir = Join-Path $projectRoot "docker"
$imageName = "hurwan0629/java-practice-intellij-project:latest"

Write-Log "Project root: $projectRoot"
Write-Log "Compose dir: $composeDir"

try {
    # 도커 버전 확인 (존재 여부)
    Write-Log "Checking Docker engine..."
    docker version | Out-Null
    Ensure-Success "docker version"

    # 루트폴더로 이동
    Push-Location $projectRoot

    # 루트 Dockerfile로 앱 이미지를 최신 상태로 다시 빌드한다.
    # Dockerfile 최신화
    Write-Log "Building app image: $imageName"
    docker build -t $imageName .
    Ensure-Success "docker build"

    # docker/ 파일로 이동
    Push-Location $composeDir

    # DB 컨테이너가 꺼져 있으면 먼저 띄우고, 이미 떠 있으면 그대로 둔다.
    # dbContainerId 에 postgres_container 식별값(Id) 뽑아줌
    $dbContainerId = docker compose ps -q postgres_container
    Ensure-Success "docker compose ps -q postgres_container"

    # dbContainerId 이 nullish이면 postgres_container 실행 패스
    if ([string]::IsNullOrWhiteSpace($dbContainerId)) {
        Write-Log "postgres_container is not running. Starting database service..."
        docker compose up -d postgres_container
        Ensure-Success "docker compose up -d postgres_container"
    }
    else {
        Write-Log "postgres_container is already running. Keeping database as-is."
    }

    # app 서비스만 다시 올린다. compose 설정과 depends_on 조건에 따라 DB 준비를 기다린다.
    Write-Log "Recreating app service..."
    docker compose up -d app
    Ensure-Success "docker compose up -d app"

    Write-Log "Current compose status:"
    docker compose ps
    Ensure-Success "docker compose ps"

    Write-Log "Deployment script completed."
}
catch {
    Write-Error $_
    exit 1
}
finally {
    if ((Get-Location).Path -eq $composeDir) {
        Pop-Location
    }

    if ((Get-Location).Path -eq $projectRoot) {
        Pop-Location
    }
}

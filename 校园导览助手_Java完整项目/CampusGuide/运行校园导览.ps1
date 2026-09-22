param(
    [switch]$仅构建,
    [switch]$跳过清理
)

$ErrorActionPreference = "Stop"
$OutputEncoding = [System.Text.UTF8Encoding]::new($false)
[Console]::InputEncoding = [System.Text.UTF8Encoding]::new($false)
[Console]::OutputEncoding = [System.Text.UTF8Encoding]::new($false)

function Write-阶段([string]$内容) {
    Write-Host "`n[校园导览] $内容" -ForegroundColor Cyan
}

Push-Location $PSScriptRoot
try {
    Write-阶段 "检查 Java 与 Android 构建环境"
    if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
        throw "未找到 Java。请在 Android Studio 中安装 JDK 17，并正确设置 JAVA_HOME。"
    }

    if (-not (Test-Path ".\gradlew.bat")) {
        throw "项目根目录缺少 gradlew.bat。"
    }

    $任务 = @()
    if (-not $跳过清理) { $任务 += "clean" }
    $任务 += "assembleDebug"

    Write-阶段 "开始构建 Debug APK（首次运行会下载 Gradle 与依赖）"
    & ".\gradlew.bat" @任务 "--console=plain"
    if ($LASTEXITCODE -ne 0) {
        throw "Gradle 构建失败，退出代码：$LASTEXITCODE"
    }

    $APK = Join-Path $PSScriptRoot "app\build\outputs\apk\debug\app-debug.apk"
    if (-not (Test-Path $APK)) {
        throw "构建结束但未找到 APK：$APK"
    }
    Write-Host "`n构建成功：$APK" -ForegroundColor Green

    if ($仅构建) {
        Write-Host "已按参数要求仅完成构建。" -ForegroundColor Yellow
        exit 0
    }

    $ADB命令 = Get-Command adb -ErrorAction SilentlyContinue
    if (-not $ADB命令 -and $env:ANDROID_HOME) {
        $候选ADB = Join-Path $env:ANDROID_HOME "platform-tools\adb.exe"
        if (Test-Path $候选ADB) { $ADB命令 = Get-Item $候选ADB }
    }
    if (-not $ADB命令 -and $env:ANDROID_SDK_ROOT) {
        $候选ADB = Join-Path $env:ANDROID_SDK_ROOT "platform-tools\adb.exe"
        if (Test-Path $候选ADB) { $ADB命令 = Get-Item $候选ADB }
    }

    if (-not $ADB命令) {
        Write-Host "未检测到 adb，APK 已生成。可在 Android Studio 中启动模拟器后手动安装。" -ForegroundColor Yellow
        exit 0
    }

    $ADB路径 = $ADB命令.Source
    if (-not $ADB路径) { $ADB路径 = $ADB命令.FullName }
    $设备 = & $ADB路径 devices | Select-String "`tdevice$"
    if (-not $设备) {
        Write-Host "未发现已连接设备/模拟器，APK 已生成，暂不自动安装。" -ForegroundColor Yellow
        exit 0
    }

    Write-阶段 "安装并启动校园导览助手"
    & $ADB路径 install -r $APK
    if ($LASTEXITCODE -ne 0) { throw "APK 安装失败。" }
    & $ADB路径 shell am start -n "com.example.campusguide/.LoginActivity"
    if ($LASTEXITCODE -ne 0) { throw "应用已安装，但自动启动失败。" }
    Write-Host "应用已成功启动。演示账号：20260001，密码：123456" -ForegroundColor Green
}
catch {
    Write-Host "`n运行失败：$($_.Exception.Message)" -ForegroundColor Red
    Write-Host "建议使用 Android Studio 打开项目，并确认 SDK 34、JDK 17 可用。" -ForegroundColor Yellow
    exit 1
}
finally {
    Pop-Location
}

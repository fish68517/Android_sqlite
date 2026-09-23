param(
    [string]$PackageName = "com.Health",
    [string]$OutputFile = "ai_model_debug.log"
)

$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$outputPath = Join-Path $projectRoot $OutputFile
$tempDir = Join-Path $projectRoot ".tmp_ai_logs"
$remotePattern = "/sdcard/Android/data/$PackageName/files/Documents/health_logs/health_debug_*.log"

New-Item -ItemType Directory -Force -Path $tempDir | Out-Null

try {
    $remoteFilesRaw = & adb shell "ls -1 $remotePattern 2>/dev/null"
    $remoteFiles = @($remoteFilesRaw | Where-Object { $_ -match "health_debug_.*\.log" } | ForEach-Object { $_.Trim() })

    if ($LASTEXITCODE -ne 0 -or $remoteFiles.Count -eq 0) {
        "No runtime AI log file was found. Start the app, trigger an AI request, then run .\gradlew.bat collectAiDebugLog again." |
            Set-Content -Path $outputPath -Encoding UTF8
        Write-Output "Saved hint file to $outputPath"
        exit 1
    }

    $latestRemote = $remoteFiles | Sort-Object | Select-Object -Last 1
    $localCopy = Join-Path $tempDir ([System.IO.Path]::GetFileName($latestRemote))

    & adb pull $latestRemote $localCopy | Out-Null
    if ($LASTEXITCODE -ne 0 -or -not (Test-Path $localCopy)) {
        throw "adb pull failed for $latestRemote"
    }

    $aiLines = Get-Content -Path $localCopy | Where-Object { $_ -match "SiliconFlowAi" }
    if ($aiLines.Count -eq 0) {
        Get-Content -Path $localCopy | Set-Content -Path $outputPath -Encoding UTF8
    } else {
        $aiLines | Set-Content -Path $outputPath -Encoding UTF8
    }

    Write-Output "Saved AI debug log to $outputPath"
}
finally {
    if (Test-Path $tempDir) {
        Remove-Item -LiteralPath $tempDir -Recurse -Force
    }
}

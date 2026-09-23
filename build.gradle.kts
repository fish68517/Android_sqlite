// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.1" apply false
}

tasks.register("collectAiDebugLog", org.gradle.api.tasks.Exec::class) {
    group = "verification"
    description = "Pull the latest AI debug log from a connected device into the project root."
    workingDir = rootDir
    commandLine(
        "powershell",
        "-ExecutionPolicy",
        "Bypass",
        "-File",
        rootProject.file("pull_ai_debug_log.ps1").absolutePath
    )
}

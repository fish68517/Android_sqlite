pluginManagement {
    repositories {
        maven ( url = "https://maven.aliyun.com/nexus/content/groups/public/")
        maven ( url = "https://maven.aliyun.com/repository/jcenter")
        maven ( url = "https://maven.aliyun.com/repository/gradle-plugin")
        maven ( url = "https://jitpack.io")
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven ( url = "https://maven.aliyun.com/nexus/content/groups/public/")
        maven ( url = "https://maven.aliyun.com/repository/jcenter")
        maven ( url = "https://maven.aliyun.com/repository/gradle-plugin")
        maven ( url = "https://jitpack.io")
        google()
        mavenCentral()

        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "FashionMall"
include(":app")
 
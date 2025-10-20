plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.application"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.application"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    // 启用视图绑定 (View Binding) 以简化UI操作
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // 基础UI库
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")

    // --- 任务相关依赖 ---

    // 任务: MVVM 架构 & 离线模式 (Room) - 级别 3
    implementation ("androidx.lifecycle:lifecycle-viewmodel:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-livedata:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-common-java8:2.6.2")

    // 添加这行来解决 "无法访问ListenableFuture" 的问题
    implementation("com.google.guava:guava:31.0.1-android")
    implementation ("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // 任务: 下拉刷新 (SwipeRefreshLayout) - 级别 2
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")



    // 任务: 复杂动画 (MotionLayout) - 级别 3
    // (已包含在 constraintlayout 中)
    // 任务: 用于延迟任务的 WorkManager - 级别 3
    implementation ("androidx.work:work-runtime:2.9.0")

    // 任务: 单元/UI 测试 - 级别 3
    implementation ("junit:junit:4.13.2")
    implementation ("androidx.test.ext:junit:1.1.5")
    implementation ("androidx.test.espresso:espresso-core:3.5.1")
    // LiveData testing
    implementation ("androidx.arch.core:core-testing:2.2.0")



    // 任务: 欢迎屏幕 (Splash Screen) - 级别 1
    implementation ("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.navigation:navigation-fragment:2.9.5")
    implementation("androidx.navigation:navigation-ui:2.9.5")


    // 图片加载库 Glide
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")

}
// 放置在 dependencies { ... } 代码块之后
configurations.all {
    // Kotlin 语法
    exclude(group = "com.intellij", module = "annotations")
}
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

        implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")



    // Stomp Protocol for Android
    implementation ("com.github.NaikSoftware:StompProtocolAndroid:1.6.6")

    implementation ("com.squareup.okhttp3:okhttp:4.10.0")

    implementation ("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation ("io.reactivex.rxjava2:rxandroid:2.1.1")

    // MPAndroidChart

    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Navigation Components
    implementation("androidx.navigation:navigation-fragment:2.7.7")
// 请使用最新版本
    implementation("androidx.navigation:navigation-ui:2.7.7")

    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
// 请使用最新版本


    // --- 任务相关依赖 ---

    // 任务: 卡片视图 (CardView) - 级别 1
    implementation ("androidx.cardview:cardview:1.0.0")

    // 任务: 带图片的列表 (RecyclerView) - 级别 2
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    // 图片加载库 Glide
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")

    // 任务: MVVM 架构 - 级别 3
    implementation ("androidx.lifecycle:lifecycle-viewmodel:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-livedata:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-common-java8:2.6.2")

    // 任务: 离线模式 (Offline Mode with Room) - 级别 3
    implementation ("androidx.room:room-runtime:2.6.1")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")

    // 任务: 生物识别 (Biometrics) - 级别 3
    implementation ("androidx.biometric:biometric:1.2.0-alpha05")

}
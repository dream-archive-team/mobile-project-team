import java.util.Properties

// local.properties에서 GEMINI_API_KEY 읽어오기
val localProps = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

plugins {
    id("com.android.application")
    kotlin("android") version "1.8.0" apply false
}

android {
    namespace = "com.example.mobileteamapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mobileteamapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            val apiKey: String? = localProps.getProperty("GEMINI_API_KEY")
            buildConfigField("String", "GEMINI_API_KEY", "\"${apiKey ?: ""}\"")
        }
        release {
            val apiKey: String? = localProps.getProperty("GEMINI_API_KEY")
            buildConfigField("String", "GEMINI_API_KEY", "\"${apiKey ?: ""}\"")
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

    dependencies {
        // UI, 레이아웃
        implementation("androidx.appcompat:appcompat:1.7.0")
        implementation("com.google.android.material:material:1.6.1")
        implementation("androidx.constraintlayout:constraintlayout:2.2.1")

        // 네트워크/API
        implementation("com.squareup.okhttp3:okhttp:4.12.0")
        implementation("org.json:json:20231013")
        implementation("com.kakao.sdk:v2-user:2.15.0")
        implementation("com.kakao.sdk:v2-auth:2.15.0")

        // 데이터베이스 (Room)
        implementation("androidx.room:room-runtime:2.6.1")
        annotationProcessor("androidx.room:room-compiler:2.6.1")

        // 테스트
        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.2.1")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    }
}


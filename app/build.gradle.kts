plugins {
    id("com.android.application")
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

    dependencies {
        implementation("com.google.android.material:material:1.6.1" )
        implementation("com.kakao.sdk:v2-user:2.15.0")
        // 필요한 다른 라이브러리도 여기에 추가
    }
}

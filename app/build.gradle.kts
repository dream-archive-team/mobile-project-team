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

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            buildConfigField("String", "GEMINI_API_KEY", "\"${project.properties["GEMINI_API_KEY"]}\"")
        }
        release {
            buildConfigField("String", "GEMINI_API_KEY", "\"${project.properties["GEMINI_API_KEY"]}\"")
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
}
dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    // Gemini API 요청용 HTTP 클라이언트
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // JSON 객체 구성용 라이브러리
    implementation("org.json:json:20231013")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
// Kotlin 표준 라이브러리의 Properties 클래스를 임포트합니다.
// 로컬 환경 설정 파일(local.properties)을 읽어올 때 사용됩니다.
import java.util.Properties

// ➊ local.properties 파일을 읽어서 Properties 객체에 저장
//    - 프로젝트 루트에 있는 local.properties 파일을 찾아
//    - 파일이 존재하면 입력 스트림을 통해 내용을 로드합니다.
val localProps = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

plugins {
    // Android 애플리케이션 플러그인 적용
    id("com.android.application")
}

android {
    // 애플리케이션의 네임스페이스(패키지) 지정
    namespace = "com.example.mobileteamapp"
    // 컴파일할 Android SDK 버전
    compileSdk = 34

    defaultConfig {
        // 애플리케이션 ID (고유 식별자)
        applicationId = "com.example.mobileteamapp"
        // 최소 지원 SDK 버전
        minSdk = 24
        // 타겟 SDK 버전
        targetSdk = 34
        // 버전 코드 및 이름
        versionCode = 1
        versionName = "1.0"

        // 테스트 러너 설정
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        // BuildConfig 클래스 생성 기능 활성화
        // → buildConfigField를 사용하기 위해 필요합니다.
        buildConfig = true
    }

    buildTypes {
        debug {
            // ➋ localProps에서 API 키를 꺼내와 BuildConfig에 주입
            //    - debug 빌드 타입에서만 적용됩니다.
            val apiKey = localProps.getProperty("GEMINI_API_KEY")
            buildConfigField("String", "GEMINI_API_KEY", "\"$apiKey\"")
        }
        release {
            // ➌ release 빌드 타입에서도 동일하게 키 주입
            val apiKey = localProps.getProperty("GEMINI_API_KEY")
            buildConfigField("String", "GEMINI_API_KEY", "\"$apiKey\"")

            // 코드 난독화 및 최적화 설정
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        // Java 1.8 호환성을 위해 설정
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // AndroidX AppCompat 라이브러리 (백워드 호환 UI)
    implementation("androidx.appcompat:appcompat:1.7.0")
    // 머티리얼 디자인 컴포넌트
    implementation("com.google.android.material:material:1.12.0")
    // ConstraintLayout 레이아웃 라이브러리
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    // Gemini API 호출을 위한 HTTP 클라이언트 (OkHttp)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // JSON 객체 생성/파싱용 라이브러리
    implementation("org.json:json:20231013")

    // 단위 테스트 및 안드로이드 테스트 의존성
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}

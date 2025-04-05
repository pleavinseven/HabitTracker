plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.pleavinseven"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.pleavinseven"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.coreKtx)
    implementation(libs.lifecycleRuntimeKtx)
    implementation(libs.activityCompose)
    implementation(platform(libs.composeBom))
    implementation(libs.composeUi)
    implementation(libs.composeUiGraphics)
    implementation(libs.composeUiToolingPreview)
    implementation(libs.material3)
    implementation(libs.lifecycleViewModelCompose)
    implementation(libs.activityKtx)
    implementation(libs.navigationCompose)
    implementation(libs.materialIconsExtended)
    implementation(libs.workRuntimeKtx)

    // Vico dependencies
    implementation(libs.vicoCompose)
    implementation(libs.vicoComposeM2)
    implementation(libs.vicoComposeM3)
    implementation(libs.vicoCore)

    // Room dependencies
    implementation(libs.roomRuntime)
    implementation(libs.roomKtx)
    testImplementation(libs.roomTesting)
    implementation(libs.roomPaging)
    ksp(libs.roomCompiler)

    // Test dependencies
    testImplementation(libs.junit)
    testImplementation(libs.coreKtxTest)
    testImplementation(libs.extJunitKtx)
    androidTestImplementation(libs.extJunitKtx)
    androidTestImplementation(libs.espressoCore)
    androidTestImplementation(platform(libs.composeBom))
    androidTestImplementation(libs.composeUiTestJunit4)
    testImplementation(libs.coroutinesTest)
    testImplementation(libs.coroutinesCore)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.mockkAndroid)
    testImplementation(libs.workTesting)
}
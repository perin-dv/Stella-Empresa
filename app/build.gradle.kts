 plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
    id("com.google.gms.google-services")
    id ("kotlin-parcelize")

}
    android {
        namespace = "com.example.stelladitaliaempresa"
        compileSdk = 35

        defaultConfig {
            applicationId = "com.example.stelladitaliaempresa"
            minSdk = 26
            targetSdk = 35
            versionCode = 1
            versionName = "1.0"

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        buildFeatures {
            viewBinding = true // ✅ Requerido para Groupie moderno
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
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }

        kotlinOptions {
            jvmTarget = "21"
        }
    }

    dependencies {
        // Groupie
        implementation("com.github.lisawray.groupie:groupie:2.9.0")
        implementation("com.github.lisawray.groupie:groupie-viewbinding:2.9.0")

        // Firebase
        implementation(platform(libs.firebase.bom))
        implementation(libs.google.firebase.auth.ktx)
        implementation(libs.google.firebase.database.ktx)
        implementation(libs.google.firebase.storage.ktx)
        implementation(libs.google.firebase.firestore.ktx)

        // AndroidX e UI
        implementation(libs.androidx.appcompat)
        implementation(libs.material)
        implementation(libs.androidx.activity)
        implementation(libs.androidx.constraintlayout)

        // Room


        implementation (libs.com.google.firebase.firebase.database.ktx)
        implementation(libs.androidx.room.runtime)
        implementation(libs.androidx.lifecycle.livedata.ktx)
        implementation(libs.androidx.lifecycle.viewmodel.ktx)
        implementation(libs.androidx.navigation.fragment.ktx)
        implementation(libs.androidx.navigation.ui.ktx)
        kapt(libs.androidx.room.compiler)
        implementation(libs.androidx.room.ktx)
        implementation (libs.gson)


        // Imagens
        implementation(libs.circleimageview)
        implementation(libs.picasso)
        implementation(libs.glide)
        //noinspection UseTomlInstead
        implementation("com.airbnb.android:lottie:6.1.0")

        // Testes
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
    }

    configurations.all {
        resolutionStrategy {
            force("org.jetbrains:annotations:23.0.0")
            exclude("com.intellij", "annotations")
        }
    }


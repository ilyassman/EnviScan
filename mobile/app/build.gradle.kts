plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.sonarqube") version "3.5.0.2730"
}

android {
    namespace = "com.example.planttest2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.planttest2"
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
    sonarqube {
        properties {
            property("sonar.projectKey", "plantapp")
            property("sonar.projectName", "plantapp")
            property("sonar.login", "sqp_8608a595ec168cae631a10df84e59e9584ab77f3")
            property("sonar.host.url", "http://localhost:9000")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation ("org.tensorflow:tensorflow-lite:2.5.0")
    implementation ("org.tensorflow:tensorflow-lite-support:0.3.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.airbnb.android:lottie:6.0.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("com.google.ar:core:1.29.0")


    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    implementation ("jp.wasabeef:recyclerview-animators:4.0.2")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    implementation ("com.google.code.gson:gson:2.8.9")

    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    // Pour le drag & drop fluide
    implementation ("com.h6ah4i.android.widget.advrecyclerview:advrecyclerview:1.0.0")
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation("io.github.sceneview:arsceneview:0.9.8")


    implementation ("com.google.android.gms:play-services-location:21.0.1")

    implementation ("de.hdodenhof:circleimageview:3.1.0")

    implementation ("com.google.mlkit:translate:17.0.1")

    implementation ("androidx.coordinatorlayout:coordinatorlayout:1.2.0")


    // OSMDroid avec exclusions en Kotlin DSL
    implementation("org.osmdroid:osmdroid-android:6.1.16") {
        exclude(group = "com.j256.ormlite", module = "ormlite-android")
        exclude(group = "com.j256.ormlite", module = "ormlite-core")
    }


    // Ajouter ces nouvelles dépendances pour OSMDroid
    //implementation("org.osmdroid:osmdroid-android:6.1.16")  // Bibliothèque principale OSMDroid
    //implementation("org.osmdroid:osmdroid-wms:6.1.16")      // Support WMS (optionnel)
    //implementation("org.osmdroid:osmdroid-mapsforge:6.1.16") // Support Mapsforge (optionnel)
    //implementation("org.osmdroid:osmdroid-geopackage:6.1.16") // Support GeoPackage (optionnel)
}
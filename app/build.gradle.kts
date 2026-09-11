import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.kapt")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.af.pb"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.af.project.base"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        multiDexEnabled = true

        val formattedDate = SimpleDateFormat("MM.dd.yyyy").format(Date())
        base.archivesName.set("AF_ProjectBase_v${versionName}(${versionCode})_${formattedDate}")
    }

    signingConfigs {
        create("release") {
            storeFile = file("key/af_projectbase.jks")
            storePassword = "123456"
            keyAlias = "key"
            keyPassword = "123456"
        }
    }

    buildTypes {
        release {
            //noinspection NotShrinkingResources
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }

        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = false
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
        }
    }

    lint {
        disable.addAll(
            hashSetOf(
                "ContentDescription",
                "HardcodedText",
                "SpUsage",
                "KotlinNullnessAnnotation",
                "NotifyDataSetChanged",
                "SetTextI18n",
                "DefaultLocale",
                "GradleDynamicVersion",
                "CustomSplashScreen",
                "ClickableViewAccessibility",
                "LockedOrientationActivity",
                "StaticFieldLeak"
            )
        )
    }

    bundle {
        language {
            enableSplit = false
        }
    }

    flavorDimensions += "default"
    productFlavors {
        create("appDebug") {
            manifestPlaceholders["ad_app_id"] = "ca-app-pub-3940256099942544~3347511713"
            buildConfigField("String", "inter_splash", "\"ca-app-pub-3940256099942544/1033173712\"")
            buildConfigField("String", "inter_splash_2f", "\"ca-app-pub-3940256099942544/1033173712\"")
            buildConfigField("String", "banner_splash", "\"ca-app-pub-3940256099942544/2014213617\"")
            buildConfigField("String", "banner_splash_2f", "\"ca-app-pub-3940256099942544/2014213617\"")
            buildConfigField("String", "native_full_splash", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_full_splash_2f", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "aoa_splash", "\"ca-app-pub-3940256099942544/9257395921\"")
            buildConfigField("String", "app_open_resume", "\"ca-app-pub-3940256099942544/9257395921\"")
            buildConfigField("String", "native_language", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_language_2f", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_language_select", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_language_select_2f", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_obd1", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_obd1_2f", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_ob12_full", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_obd2", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_obd2_2f", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_obd3", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_obd3_2f", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_ob23_full", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_ob23_full_2f", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_ob34_full", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_obd4", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "inter_function", "\"ca-app-pub-3940256099942544/1033173712\"")
            buildConfigField("String", "inter_back", "\"ca-app-pub-3940256099942544/1033173712\"")
            buildConfigField("String", "banner_all", "\"ca-app-pub-3940256099942544/2014213617\"")
            buildConfigField("String", "reward_film", "\"ca-app-pub-3940256099942544/5224354917\"")
            buildConfigField("String", "reward_top_film", "\"ca-app-pub-3940256099942544/5224354917\"")
            buildConfigField("String", "inter_cancel_paywall", "\"ca-app-pub-3940256099942544/1033173712\"")
            buildConfigField("String", "native_full_play_film", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_uninstall", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "banner_collap", "\"ca-app-pub-3940256099942544/2014213617\"")
            buildConfigField("String", "inter_home", "\"ca-app-pub-3940256099942544/1033173712\"")
            buildConfigField("String", "reward_quality", "\"ca-app-pub-3940256099942544/5224354917\"")
            buildConfigField("String", "native_full_remove", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_setting", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_exit", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "inter_exit", "\"ca-app-pub-3940256099942544/1033173712\"")
            buildConfigField("Boolean", "build_debug", "true")
        }
        create("appProduct") {
            manifestPlaceholders["ad_app_id"] = "ca-app-pub-3940256099942544~3347511713"
            buildConfigField("String", "inter_splash", "\"ca-app-pub-3940256099942544/1033173712\"")
            buildConfigField("String", "inter_splash_2f", "\"ca-app-pub-3940256099942544/1033173712\"")
            buildConfigField("String", "banner_splash", "\"ca-app-pub-3940256099942544/2014213617\"")
            buildConfigField("String", "banner_splash_2f", "\"ca-app-pub-3940256099942544/2014213617\"")
            buildConfigField("String", "native_full_splash", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_full_splash_2f", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "aoa_splash", "\"ca-app-pub-3940256099942544/9257395921\"")
            buildConfigField("String", "app_open_resume", "\"ca-app-pub-3940256099942544/9257395921\"")
            buildConfigField("String", "native_language", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_language_2f", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_language_select", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_language_select_2f", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_obd1", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_obd1_2f", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_ob12_full", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_obd2", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_obd2_2f", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_obd3", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_obd3_2f", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_ob23_full", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_ob23_full_2f", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_ob34_full", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_obd4", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "inter_function", "\"ca-app-pub-3940256099942544/1033173712\"")
            buildConfigField("String", "inter_back", "\"ca-app-pub-3940256099942544/1033173712\"")
            buildConfigField("String", "banner_all", "\"ca-app-pub-3940256099942544/2014213617\"")
            buildConfigField("String", "reward_film", "\"ca-app-pub-3940256099942544/5224354917\"")
            buildConfigField("String", "reward_top_film", "\"ca-app-pub-3940256099942544/5224354917\"")
            buildConfigField("String", "inter_cancel_paywall", "\"ca-app-pub-3940256099942544/1033173712\"")
            buildConfigField("String", "native_full_play_film", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_uninstall", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "banner_collap", "\"ca-app-pub-3940256099942544/2014213617\"")
            buildConfigField("String", "inter_home", "\"ca-app-pub-3940256099942544/1033173712\"")
            buildConfigField("String", "reward_quality", "\"ca-app-pub-3940256099942544/5224354917\"")
            buildConfigField("String", "native_full_remove", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_setting", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "native_exit", "\"ca-app-pub-3940256099942544/2247696110\"")
            buildConfigField("String", "inter_exit", "\"ca-app-pub-3940256099942544/1033173712\"")
            buildConfigField("Boolean", "build_debug", "false")
        }
    }
}

configurations.all {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-android-extensions-runtime")
}

dependencies {
    implementation(project(":ads"))
    implementation(project(":network"))
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.so"))))
    implementation("androidx.core:core-ktx:1.19.0")
    implementation("androidx.appcompat:appcompat:1.8.0")
    implementation("com.google.android.material:material:1.14.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")

    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.preference:preference-ktx:1.2.1")

    // hilt
    implementation("com.google.dagger:hilt-android:2.60.1")
    kapt("com.google.dagger:hilt-compiler:2.60.1")

    // rxkotlin
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")

    //Recycler view
    implementation("androidx.recyclerview:recyclerview:1.4.0")

    //Fragment
    implementation("androidx.fragment:fragment-ktx:1.9.0")

    implementation("androidx.room:room-ktx:2.8.4")
    ksp("androidx.room:room-compiler:2.8.4")

    //Glide
    implementation("com.github.bumptech.glide:glide:5.0.9")
    ksp("com.github.bumptech.glide:ksp:5.0.9")

    //Event bus
    implementation("org.greenrobot:eventbus:3.3.1")

    //Firebase
    implementation("com.google.firebase:firebase-config:23.1.0")
    implementation(platform("com.google.firebase:firebase-bom:34.18.0"))
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")

    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.11.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.11.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.11.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.11.0")

    implementation("com.karumi:dexter:6.2.3")

    //Lottie
    implementation("com.airbnb.android:lottie:6.7.1")

    //Rounded ImageView
    implementation("com.makeramen:roundedimageview:2.3.0")

    //DotsIndicator
    implementation("com.tbuonomo:dotsindicator:5.1.1")

    //shimmer
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    // Media3 ExoPlayer
    implementation("androidx.media3:media3-exoplayer:1.4.1")
    implementation("androidx.media3:media3-exoplayer-hls:1.4.1")
    implementation("androidx.media3:media3-ui:1.4.1")

    // Flexbox
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    //AdMob
    implementation("com.google.android.gms:play-services-ads:25.4.0")

    //UMP
    implementation("com.google.android.ump:user-messaging-platform:4.0.0")

    //Facebook SDK
    implementation("com.facebook.android:facebook-android-sdk:18.3.0")

    // In-App Update
    implementation("com.google.android.play:app-update-ktx:2.1.0")

    //RatingBar
    implementation("com.github.wdsqjq:AndRatingBar:1.0.6")

    //Blur
    implementation("com.github.Dimezis:BlurView:version-2.0.3")

}
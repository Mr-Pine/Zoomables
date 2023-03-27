import org.jetbrains.kotlin.gradle.tasks.*

plugins {
    alias(libs.plugins.android.library)
    kotlin("android")
    `maven-publish`
    alias(libs.plugins.dokka)
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 22
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

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }
    namespace = "de.mr_pine.zoomables"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs += listOf("-Xexplicit-api=strict")
}

dependencies {

    implementation(libs.androidx.ktx)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.util)
    implementation(libs.compose.material)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

/*ext {
    PUBLISH_GROUP_ID = "de.mr-pine.utils"
    PUBLISH_VERSION = "1.1.2"
    PUBLISH_ARTIFACT_ID = "zoomables"
}*/

//apply(from = "${rootProject.projectDir}/scripts/publish-module.gradle")

// Because the components are created only during the afterEvaluate phase, you must
// configure your publications using the afterEvaluate() lifecycle method.
/*
afterEvaluate {
    publishing {
        publications {
            mavenLocal(MavenPublication) {
                // Applies the component for the release build variant.
                from(components.release)

                // You can then customize attributes of the publication as shown below.
                groupId = PUBLISH_GROUP_ID
                artifactId = PUBLISH_ARTIFACT_ID
                version = PUBLISH_VERSION
            }
        }
    }
}*/

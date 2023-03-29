import org.jetbrains.kotlin.gradle.tasks.*

plugins {
    alias(libs.plugins.android.library)
    kotlin("android")
    `maven-publish`
    alias(libs.plugins.dokka)
    signing
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

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
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

publishing {
    publications {
        register<MavenPublication>("zoomables") {
            groupId = publishData.artifact.group
            artifactId = publishData.artifact.id
            version = publishData.artifact.version

            afterEvaluate {
                from(components["release"])
            }

            pom {
                description.set("A library provides Composables that handle nice and smooth zooming behaviour for you")
                name.set(publishData.artifact.id)
                url.set("https://github.com/Mr-Pine/Zoomables")

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://github.com/Mr-Pine/Zoomables/blob/master/LICENSE")
                    }
                }

                developers {
                    developer {
                        id.set("Mr-Pine")
                    }
                }

                scm {
                    connection.set("scm:git:github.com/Mr-Pine/Zoomables.git")
                    developerConnection.set("scm:git:ssh://github.com/Mr-Pine/Zoomables.git")
                    url.set("https://github.com/Mr-Pine/Zoomables")
                }
            }
        }

    }
}

signing {
    useGpgCmd()
    sign(publishing.publications)
}
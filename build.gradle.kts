// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.dokka) apply false
    kotlin("android") version libs.versions.kotlin.get() apply false
    alias(libs.plugins.nexus.publish)
}

tasks.create<Delete>("clean") {
    delete(rootProject.buildDir)
}

nexusPublishing {
    repositoryDescription.set("zoomables:$version")
    this.repositories {
        sonatype {
            stagingProfileId.set(publishData.sonatypeStagingProfileId)
            username.set(publishData.ossrh.username)
            password.set(publishData.ossrh.password)
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}
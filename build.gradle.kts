val appName = "BuchungsstapelKontieren"

plugins {
    id("application")
    id("org.jetbrains.kotlin.jvm") version "1.6.21"
    id("org.openjfx.javafxplugin") version "0.0.8"
}

group "org.example"
version "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
val compileTestKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks

compileKotlin.kotlinOptions {
    jvmTarget = "11"
}
compileTestKotlin.kotlinOptions {
    jvmTarget = "11"
}

javafx {
    version = "18.0.1"
    modules = listOf("javafx.controls", "javafx.graphics")
}
//Thanks for using https://jar-download.com
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("no.tornado:tornadofx:1.7.20")
    implementation("io.github.microutils:kotlin-logging:2.1.21")
    implementation("org.slf4j:slf4j-simple:1.7.36")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

tasks {
    test {
        testLogging.showExceptions = true
        useJUnitPlatform()
    }
    jar {
        manifest {
            attributes["Main-Class"] = "KontierenApp"
        }
        archiveBaseName.set(appName)
    }
}

// Build Release
val releaseDir = "${buildDir}/release"
task<Copy>("copyReleaseLibs") {
    from(configurations.runtimeClasspath) {
        exclude("javafx-*") // Already in JRE
    }
    into("$releaseDir/app/libs")
    println("$releaseDir/app/libs")
}

task<Copy>("copyReleaseApp") {
    from("${buildDir}/libs/${appName}.jar")
    into("$releaseDir/app/")
}.dependsOn("build")

task<Copy>("copyReleaseJRE") {
    from(layout.projectDirectory.dir("release-template"))
    into("$releaseDir/")
    println("asd: " + layout.projectDirectory.dir("release-template"))
}

task<Zip>("zipRelease") {
    archiveBaseName.set(appName)
    destinationDirectory.set(file("$releaseDir/zip"))
    from(releaseDir) {
        exclude("zip/*") //
        exclude("zip") //
    }
}.dependsOn("build", "copyReleaseJRE", "copyReleaseApp", "copyReleaseLibs")

task<GradleBuild>("buildRelease") {
    dependsOn("zipRelease")
}
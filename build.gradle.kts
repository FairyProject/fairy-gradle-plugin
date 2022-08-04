import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    `java-gradle-plugin`
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.apache.maven:maven-plugin-api:3.8.5")
    implementation("khttp:khttp:1.0.0")
    implementation("org.ow2.asm:asm:9.3")
    implementation("org.ow2.asm:asm-commons:9.3")
}

gradlePlugin {
    plugins {
        create("fairyPlugin") {
            displayName = "Fairy plugin"
            description = "A Gradle plugin that provides ability to manage fairy project easily."
            id = "io.fairyproject"
            implementationClass = "io.fairyproject.gradle.FairyGradlePlugin"
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    id("com.gradle.plugin-publish") version "0.14.0"
    `java-gradle-plugin`
    `maven-publish`
}

group = "io.fairyproject"
version = "1.3.0b4"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.apache.maven:maven-plugin-api:3.8.5")
    implementation("khttp:khttp:1.0.0")
    implementation("org.ow2.asm:asm:9.3")
    implementation("org.ow2.asm:asm-commons:9.3")
    implementation("com.google.code.gson:gson:2.9.1")
}

gradlePlugin {
    plugins {
        create("fairy") {
            displayName = "Fairy plugin"
            description = "A Gradle plugin that provides ability to manage fairy project easily."
            id = "io.fairyproject"
            implementationClass = "io.fairyproject.gradle.FairyGradlePlugin"
        }
    }
}

pluginBundle {
    tags = listOf("fairy", "bukkit", "minecraft")
    website = "https://github.com/FairyProject/fairy"
    vcsUrl = "https://github.com/FairyProject/fairy"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
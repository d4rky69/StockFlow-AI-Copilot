plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
    id("org.springframework.boot") version "3.2.5"

}

group = "com.stockflow"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(org.gradle.jvm.toolchain.JavaLanguageVersion.of(21))
    }
}

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "21"
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.5")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.2.5")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.2.5")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.5")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.23")
    runtimeOnly("org.postgresql:postgresql:42.6.0")
}

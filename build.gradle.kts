import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.5.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.21"
    kotlin("plugin.spring") version "1.5.21"
    application
}

java.sourceCompatibility = JavaVersion.VERSION_11
java.targetCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.redisson:redisson:3.18.1")
    implementation("org.testcontainers:testcontainers:1.17.6")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    implementation("io.mockk:mockk:1.12.0")
    testImplementation("io.cucumber:cucumber-junit:7.8.1")
    testImplementation("io.cucumber:cucumber-java:7.8.1")
    testImplementation("io.cucumber:cucumber-spring:7.8.1")
    implementation("org.springdoc:springdoc-openapi-webflux-ui:1.6.13")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

application {
    mainClass.set("notificationservice.App")
}
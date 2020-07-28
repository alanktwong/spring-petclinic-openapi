import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.samples.petclinic.build.Versions

description = "Open API version of the Spring Petclinic application with Kotlin"
group = "org.springframework.samples"
// Align with Spring Version
version = "2.3.0.RELEASE"

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

plugins {
    val kotlinVersion = "1.3.72"
    id("org.springframework.boot") version "2.3.0.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("com.google.cloud.tools.jib") version "1.3.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
}

val boostrapVersion = "3.3.6"
val jQueryVersion = "2.2.4"
val jQueryUIVersion = "1.11.4"

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = Versions.java
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }
    test {
        useJUnitPlatform()
        testLogging {
            events("FAILED", "SKIPPED")
        }
    }
    wrapper {
        gradleVersion = Versions.gradle
    }
}

repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("https://repo.spring.io/snapshot") }
    maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("javax.cache:cache-api")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.webjars:webjars-locator-core")
    implementation("org.webjars:jquery:$jQueryVersion")
    implementation("org.webjars:jquery-ui:$jQueryUIVersion")
    implementation("org.webjars:bootstrap:$boostrapVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("mysql:mysql-connector-java")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
}

jib {
    to {
        image = "springcommunity/spring-petclinic-openapi"
        tags = setOf(project.version.toString(), "latest")
    }
}


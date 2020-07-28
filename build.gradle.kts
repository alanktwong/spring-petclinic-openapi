import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.samples.petclinic.build.Versions
import org.springframework.samples.petclinic.build.assertJavaVersions

description = "Open API version of the Spring Petclinic application with Kotlin"
group = "org.springframework.samples"
// Align with Spring Version
version = "2.2.5.RELEASE"

plugins {
    val kotlinVersion = "1.3.72"
    // Increase spring boot version?
    id("org.springframework.boot") version "2.2.5.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("com.google.cloud.tools.jib") version "1.3.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
}

java {
    assertJavaVersions()
    sourceCompatibility = JavaVersion.VERSION_11
}


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

// add checkstyle
// https://github.com/alanktwong/spring-petclinic-openapi/issues/10

// add jacoco
// https://github.com/alanktwong/spring-petclinic-openapi/issues/6

// add OpenAPI generator
// https://github.com/alanktwong/spring-petclinic-openapi/issues/7

repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("https://repo.spring.io/snapshot") }
    maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.data:spring-data-jdbc-core:${Versions.springDataJdbc}")
    // {
    //     exclude(group = "org.springframework")
    // }
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    // implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("javax.cache:cache-api")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("io.springfox:springfox-swagger2:${Versions.springfox}")
    implementation("io.springfox:springfox-swagger-ui:${Versions.springfox}")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.jayway.jsonpath:json-path")

    testImplementation("org.mockito:mockito-core")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    runtime("org.hsqldb:hsqldb")
    // runtime("mysql:mysql-connector-java")
    // runtime("org.postgresql:postgresql")

    runtime("javax.xml.bind:jaxb-api:${Versions.jaxb}")
    runtime("com.sun.xml.bind:jaxb-core:${Versions.jaxb}")
    runtime("com.sun.xml.bind:jaxb-impl:${Versions.jaxb}")
    runtime("org.glassfish.jaxb:jaxb-runtime:${Versions.jaxb}")
    runtime("javax.activation:activation:${Versions.activation}")
    // developmentOnly("org.springframework.boot:spring-boot-devtools")
}

// https://github.com/alanktwong/spring-petclinic-openapi/issues/2
jib {
    to {
        image = "springcommunity/spring-petclinic-openapi"
        tags = setOf(project.version.toString(), "latest")
    }
}


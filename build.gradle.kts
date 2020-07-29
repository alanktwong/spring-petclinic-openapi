import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.samples.petclinic.build.Versions
import org.springframework.samples.petclinic.build.assertJavaVersions
import org.springframework.samples.petclinic.build.skipFormat
import org.springframework.samples.petclinic.build.skipErrorProne

description = "Open API version of the Spring Petclinic application with Kotlin"
group = "org.springframework.samples"
// Align with Spring Version
version = "2.2.5.RELEASE"

plugins {
    java
    checkstyle
    jacoco
    // Increase spring boot version?
    id("org.springframework.boot") version "2.2.5.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("com.google.cloud.tools.jib") version "1.3.0"

    val kotlinVersion = "1.3.72"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    kotlin("plugin.allopen") version kotlinVersion
    kotlin("kapt") version kotlinVersion

    id("com.diffplug.gradle.spotless") version "3.27.1"
    id("org.jlleitschuh.gradle.ktlint") version "9.3.0"
    id("io.gitlab.arturbosch.detekt") version "1.10.0"
    id("net.ltgt.errorprone") version "1.1.1" apply false
}

if (!skipErrorProne()) {
    apply(plugin = "net.ltgt.errorprone")
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
    withType<AbstractArchiveTask> {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
    test {
        jvmArgs = listOf("-XX:TieredStopAtLevel=1")
        useJUnitPlatform()
        testLogging {
            events("FAILED", "SKIPPED")
        }
    }
    wrapper {
        gradleVersion = Versions.gradle
    }
}

// add OpenAPI generator
// https://github.com/alanktwong/spring-petclinic-openapi/issues/7

repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("https://repo.spring.io/snapshot") }
    maven { url = uri("https://repo.spring.io/milestone") }
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.MappedSuperclass")
}

ktlint {
    ignoreFailures.set(false)
}

tasks.named("compileKotlin") {
    dependsOn(":ktlintFormat")
}

detekt {
    toolVersion = Versions.detekt
    // fail build on any finding
    failFast = false
    // preconfigure defaults
    buildUponDefaultConfig = true
    // point to your custom config defining rules to run, overwriting default behavior
    config = files("$projectDir/config/detekt/detekt.yml")
    // a way of suppressing issues before introducing detekt
    // baseline = file("$projectDir/gradle/baseline.xml")

    reports {
        html {
            // observe findings in your browser with structure and code snippets
            enabled = true
            destination = file("$buildDir/reports/detekt/detekt.html")
        }
        xml {
            // checkstyle like format mainly for integrations like Jenkins
            enabled = true
            destination = file("$buildDir/reports/detekt/detekt.xml")
        }
        txt {
            // similar to the console output, contains issue signature to manually edit baseline files
            enabled = false
        }
    }
}

dependencies {
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
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
    testImplementation("com.ninja-squad:springmockk:${Versions.springMockk}")

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

// Add spotless
spotless {
    java {
        eclipse(Versions.eclipse).configFile("$rootDir/config/checkstyle/eclipse-formatter.xml")
        removeUnusedImports()
    }
}

// add checkstyle
// https://github.com/alanktwong/spring-petclinic-openapi/issues/10
checkstyle {
    toolVersion = Versions.checkstyle
    configFile = file("$rootDir/config/checkstyle/checkstyle.xml")

    configProperties = mapOf(
        "config_loc" to file("$rootDir/config/checkstyle/suppressions.xml").absolutePath
    )
    isIgnoreFailures = false
    maxWarnings = 0
    maxErrors = 0
}

tasks {
    checkstyleMain {
        exclude("**/package-info.java")
    }
}

// add jacoco
jacoco {
    toolVersion = Versions.jacoco
}

tasks {
    jacocoTestCoverageVerification {
        violationRules {
            rule {
                element = "SOURCEFILE"
                excludes = listOf("*Application.java", "**/model/*.java", "*Config.java", "**/CallMonitoringAspect.java")

                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = BigDecimal(0.65)
                }
            }
        }
    }
    jacocoTestReport {
        reports {
            html.isEnabled = true
            html.destination = file("$buildDir/reports/jacoco/report.html")
        }
    }
}
tasks.named("test") {
    finalizedBy(":jacocoTestReport")
}

// https://github.com/alanktwong/spring-petclinic-openapi/issues/2
jib {
    to {
        image = "springcommunity/spring-petclinic-openapi"
        tags = setOf(project.version.toString(), "latest")
    }
}

afterEvaluate {
    if (!skipFormat()) {
        tasks.named("checkstyleMain") {
            dependsOn(":spotlessApply")
        }
        tasks.named("checkstyleTest") {
            dependsOn(":spotlessApply")
        }
        tasks.named("spotlessCheck") {
            dependsOn(":spotlessApply")
        }
    } else {
        tasks.named("checkstyleMain") {
            dependsOn(":spotlessCheck")
        }
        tasks.named("checkstyleTest") {
            dependsOn(":spotlessCheck")
        }
    }

    tasks.named("jacocoTestReport") {
        dependsOn(":jacocoTestCoverageVerification")
    }
    tasks.named("check") {
        dependsOn(":jacocoTestReport")
    }
}

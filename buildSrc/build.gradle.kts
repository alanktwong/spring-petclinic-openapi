import org.gradle.kotlin.dsl.`kotlin-dsl`
import kotlin.collections.mapOf

repositories {
    gradlePluginPortal()
    mavenCentral()
    jcenter()
}

plugins {
    `kotlin-dsl`
    java
    groovy
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("FAILED", "SKIPPED")
        }
    }
}

gradlePlugin {
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("FAILED", "SKIPPED")
        }
    }
}

val kotlinVersion = "1.3.72"
val junitJupiterVersion = "5.2.0"

dependencies {
    implementation(gradleApi())

    testCompile("org.jetbrains.kotlin:kotlin-test-junit:${kotlinVersion}")
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")

    testCompile("org.junit.jupiter:junit-jupiter-api:${junitJupiterVersion}")
    testCompile("org.junit.jupiter:junit-jupiter-params:${junitJupiterVersion}")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:${junitJupiterVersion}")
}
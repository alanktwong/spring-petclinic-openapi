package org.springframework.samples.petclinic.build

import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.Project

object Versions {
    const val java = "11"
    const val gradle = "6.5.1"
    const val springfox = "2.9.2"

    const val junit = "4.12"
    const val jaxb = "2.3.0"
    const val activation = "1.1.1"
    const val checkstyle = "8.23"
    const val eclipse = "4.10.0"

    const val jacksonDatabindNullable = "0.2.1"
    const val jacoco = "0.8.5"

    const val boostrapVersion = "3.3.6"
    const val jQueryVersion = "2.2.4"
    const val jQueryUIVersion = "1.11.4"

    const val springMockk = "1.1.3"
    const val detekt = "1.10.0"
}

fun JavaPluginExtension.assertJavaVersions() {
    assert(listOf("1.8", "11", "12", "13").contains(System.getProperty("java.specification.version")))
}

fun Project.skipErrorProne(): Boolean {
    return hasProperty("skipErrorProne")
}

fun Project.skipFormat(): Boolean {
    return hasProperty("skipFormat")
}

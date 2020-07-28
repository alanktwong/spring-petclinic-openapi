package org.springframework.samples.petclinic.build

import org.gradle.api.plugins.JavaPluginExtension

object Versions {
    const val java = "11"
    const val gradle = "6.5.1"
    const val springfox = "2.9.2"

    const val junit = "4.12"
    const val jaxb = "2.3.0"
    const val activation = "1.1.1"

    const val jacksonDatabindNullable = "0.2.1"
    const val springDataJdbc = "1.2.1.RELEASE"
    const val jacoco = "0.8.5"

    const val boostrapVersion = "3.3.6"
    const val jQueryVersion = "2.2.4"
    const val jQueryUIVersion = "1.11.4"
}

fun JavaPluginExtension.assertJavaVersions() {
    assert(listOf("1.8", "11", "12", "13").contains(System.getProperty("java.specification.version")))
}

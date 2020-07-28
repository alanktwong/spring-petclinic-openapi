package org.springframework.samples.petclinic.build

object Versions {
    const val java = "11"
    const val gradle = "6.5.1"
    const val springFox = "3.0.0"

    const val junit = "4.12"
    const val springfox = "2.9.2"

    const val jacksonDatabindNullable = "0.2.1"

    fun assertJava() {
        assert(listOf("1.8", "11", "12", "13").contains(System.getProperty("java.specification.version")))
    }
}

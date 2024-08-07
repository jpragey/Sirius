/*
 * This file was generated by the Gradle 'init' task.
 */

dependencies {
    implementation(project(":org.sirius.common"))
    implementation(project(":org.sirius.frontend"))
    implementation(project(":org.sirius.backend.core"))
    implementation(project(":org.sirius.sdk"))
    implementation(project(":org.sirius.runtime"))
    implementation("org.ow2.asm:asm:9.4")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")

    testImplementation("org.ow2.asm:asm-util:9.4")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
    testImplementation("org.mockito:mockito-core:4.1.0")
    testImplementation("org.mockito:mockito-junit-jupiter:4.1.0")
    testImplementation("org.hamcrest:hamcrest:2.2")
}

/*
plugins {
    id("org.sirius.java-conventions")
}

description = "org.sirius.backend.jvm"
*/

plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
}

application {
    mainClass.set("performancetests.Main")
}

tasks.jar {
    archiveBaseName = "mergesortJava"
    manifest {
        attributes("Main-Class" to "performancetests.Main")
    }
}

tasks.withType<org.gradle.api.tasks.compile.JavaCompile>().configureEach {
    options.compilerArgs.add("-Xlint:deprecation")
}
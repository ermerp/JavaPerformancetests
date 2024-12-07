plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
}

application {
    // Define the main class for the application.
    mainClass.set("performancetests.Main")
}

tasks.jar {
    archiveBaseName = "bankDataGenerator"
    manifest {
        attributes("Main-Class" to "performancetests.Main")
    }
}

tasks.withType<org.gradle.api.tasks.compile.JavaCompile>().configureEach {
    options.compilerArgs.add("-Xlint:deprecation")
}
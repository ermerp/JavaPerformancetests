plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("com.zaxxer:HikariCP:6.0.0")
    implementation("org.slf4j:slf4j-simple:2.0.9")
}

application {
    // Define the main class for the application.
    mainClass.set("performancetests.Main")
}

tasks.jar {
    archiveBaseName = "bankJava"
    manifest {
        attributes("Main-Class" to "performancetests.Main")
    }
}

tasks.withType<org.gradle.api.tasks.compile.JavaCompile>().configureEach {
    options.compilerArgs.add("-Xlint:deprecation")
}
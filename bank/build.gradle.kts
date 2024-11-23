plugins {
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
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
    mainClass.set("performancetests.Main")
}

tasks.jar {
    archiveBaseName.set("bankJava")
    manifest {
        attributes["Main-Class"] = "performancetests.Main"
    }
}

tasks.withType<org.gradle.api.tasks.compile.JavaCompile>().configureEach {
    options.compilerArgs.add("-Xlint:deprecation")
}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveBaseName.set("bankJava")
        archiveClassifier.set("")
        archiveVersion.set("")
    }

    named<Zip>("distZip") {
        dependsOn("shadowJar")
    }

    named<Tar>("distTar") {
        dependsOn("shadowJar")
    }

    named<CreateStartScripts>("startScripts") {
        dependsOn("shadowJar")
    }

    named<CreateStartScripts>("startShadowScripts") {
        dependsOn("jar")
    }
}
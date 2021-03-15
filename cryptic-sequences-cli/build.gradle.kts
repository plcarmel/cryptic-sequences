plugins {
  kotlin("jvm")
  id("com.github.johnrengelman.shadow") version("6.1.0")
}

group = "net.plcarmel.cryptic-sequences"
version = "2.2.4-SNAPSHOT"

repositories {
  mavenCentral()
  maven {
    url = uri("https://kotlin.bintray.com/kotlinx")
  }
}

dependencies {
  implementation(project(":cryptic-sequences-core"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.31:modular")
  implementation("org.jetbrains.kotlinx:kotlinx-cli-jvm:0.3.1")
}

tasks {
  compileJava {
    options.compilerArgs = listOf(
      "--module-path",
      classpath.asPath
    )
  }
  shadowJar {
    manifest {
      attributes["Main-Class"] = "net.plcarmel.crypticsequences.cli.MainKt"
    }
  }
  jar {
    dependsOn(shadowJar)
  }
}

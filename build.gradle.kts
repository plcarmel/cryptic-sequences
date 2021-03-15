plugins {
  id("idea")
  id("java")
  kotlin("jvm") version("1.4.31")
}

group = "net.plcarmel.cryptic-sequences"
version = "2.2.4-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  project(":cryptic-sequences-cli")
  project(":cryptic-sequences-core")
  implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.31:modular")
}

subprojects {

  apply(plugin = "org.jetbrains.kotlin.jvm")

  tasks {
    listOf(compileKotlin, compileTestKotlin).forEach {
      it {
        kotlinOptions {
          jvmTarget = "11"
        }
      }
    }
  }

  java {
    modularity.inferModulePath.set(true)
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  configure<SourceSetContainer> {
    named("main") {
      java.srcDir("src/main/java")
      java.srcDir("src/main/kotlin")
    }
    named("test") {
      java.srcDir("src/test/java")
      java.srcDir("src/test/kotlin")
    }
  }

}


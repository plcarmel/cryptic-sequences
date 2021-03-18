import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  repositories {
    mavenCentral()
  }
  val kotlinVersion: String by project
  dependencies {
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
  }
}

plugins {
  kotlin("multiplatform") version("1.4.31")
}

kotlin {

  jvm {
    allprojects {
      repositories {
        mavenCentral()
      }
      tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
          jvmTarget = "11"
        }
      }

    }

  }

}

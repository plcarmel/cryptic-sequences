plugins {
  kotlin("multiplatform")
}

kotlin {

  jvm()

  sourceSets {
    @Suppress("UNUSED_VARIABLE")
    val commonMain by getting {
      dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
      }
    }
    @Suppress("UNUSED_VARIABLE")
    val jvmTest by getting {
      val jupiterVersion = "5.7.1"
      val mockitoVersion = "3.8.0"
      dependencies {
        implementation("org.junit.jupiter:junit-jupiter-api:${jupiterVersion}")
        implementation("org.junit.jupiter:junit-jupiter-params:${jupiterVersion}")
        implementation("org.mockito:mockito-junit-jupiter:${mockitoVersion}")
        runtimeOnly("org.junit.jupiter:junit-jupiter-engine:${jupiterVersion}")
      }
    }
  }

  tasks.named<Test>("jvmTest") {
    useJUnitPlatform()
    testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(
        org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
        org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
      )
      exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
  }

}

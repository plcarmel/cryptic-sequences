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
  kotlin("multiplatform")
  id("maven-publish")
  id("signing")
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

  subprojects {

    apply(plugin = "org.jetbrains.kotlin.multiplatform")
    apply(plugin = "publishing")
    apply(plugin = "signing")

    val isJvm = publishing.publications.any { it.name == "jvm"}

    signing {
      useGpgCmd()
      if (isJvm) {
        sign(publishing.publications["jvm"])
      }
    }

    publishing {

      publications {

        withType<MavenPublication>().configureEach {
          groupId = properties["groupId"]!!.toString()
          version = properties["version"]!!.toString()
        }

        if (isJvm) {
          named<MavenPublication>("jvm") {
            pom {
              name.set("cryptic-sequences core library")
              description.set("Create small cyphers to generate random-looking unique ids")
              url.set("https://github.com/plcarmel/cryptic-sequences")
              licenses {
                license {
                  name.set("MIT")
                  url.set("https://mit-license.org/")
                  distribution.set("repo")
                }
              }
              scm {
                url.set("https://github.com/plcarmel/cryptic-sequences")
                connection.set("scm:git:git@github.com:plcarmel/cryptic-sequences.git")
                developerConnection.set("scm:git:ssh:git@github.com:plcarmel/cryptic-sequences.git")
              }
            }
          }
        }
      }

      repositories {
        maven {
          name = "Central"
          url = uri("https://s01.oss.sonatype.org/content/repositories/releases/")
          credentials {
            username = properties["ossrhUsername"]!!.toString()
            password = properties["ossrhPassword"]!!.toString()
          }
        }
        maven {
          name = "CentralSnapshot"
          url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
          credentials {
            username = properties["ossrhUsername"]!!.toString()
            password = properties["ossrhPassword"]!!.toString()
          }
        }
        maven {
          name = "Staging"
          url = uri("$buildDir/repos/staging")
        }
      }
    }
  }
}

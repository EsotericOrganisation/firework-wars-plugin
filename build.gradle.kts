import org.gradle.api.JavaVersion
import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
  java
  `java-library`

  `maven-publish`

  id("io.papermc.paperweight.userdev") version "1.7.2"
  id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.1.1"
  id("xyz.jpenilla.run-paper") version "2.3.0"

  id("io.github.goooler.shadow") version "8.1.7"
}

val groupStringSeparator = "."
val kebabCaseStringSeparator = "-"
val snakeCaseStringSeparator = "_"

fun capitalizeFirstLetter(string: String): String {
  return string.first().uppercase() + string.slice(IntRange(1, string.length - 1))
}

fun snakeCase(kebabCaseString: String): String {
  return kebabCaseString.lowercase().replace(kebabCaseStringSeparator, snakeCaseStringSeparator)
}

fun pascalCase(kebabCaseString: String): String {
  var pascalCaseString = ""

  val splitString = kebabCaseString.split(kebabCaseStringSeparator)

  for (part in splitString) {
    pascalCaseString += capitalizeFirstLetter(part)
  }

  return pascalCaseString
}

description = "A Minecraft Paper plugin that adds a firework-focused PvP gamemode."

val mainProjectAuthor = "Slqmy"
val mainestProjectAuthor = "rolyPolyVole"
val projectAuthors = listOfNotNull(mainProjectAuthor, mainestProjectAuthor)

val topLevelDomain = "net"
val projectNameString = rootProject.name

group = topLevelDomain + groupStringSeparator + mainProjectAuthor.lowercase() + groupStringSeparator + snakeCase(projectNameString)
version = "1.0.0"

val buildDirectoryString = buildDir.toString()

val projectGroupString = group.toString()
val projectVersionString = version.toString()

val javaVersion = 21
val javaVersionEnumMember = JavaVersion.valueOf("VERSION_" + javaVersion)
val paperApiVersion = "1.21"

java {
  sourceCompatibility = javaVersionEnumMember
  targetCompatibility = javaVersionEnumMember

  toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
}

repositories {
    mavenCentral()
}

dependencies {
  paperweight.paperDevBundle(paperApiVersion + "-R0.1-SNAPSHOT")

  implementation("dev.jorel" , "commandapi-bukkit-shade-mojang-mapped" , "9.5.1")
}

tasks {
  build {
    dependsOn(shadowJar)
  }

  shadowJar {
    archiveFileName = projectNameString + "-" + projectVersionString + "." + "jar"
  }

  compileJava {
    options.release = javaVersion
  }

  javadoc {
    options.encoding = Charsets.UTF_8.name()
  }
}

bukkitPluginYaml {
  authors = projectAuthors

  main = projectGroupString + groupStringSeparator + pascalCase(projectNameString)
  apiVersion = paperApiVersion

  load = BukkitPluginYaml.PluginLoadOrder.STARTUP
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = projectGroupString
            artifactId = projectNameString
            version = projectVersionString
        }
    }
}

tasks.named("publishMavenJavaPublicationToMavenLocal") {
  dependsOn(tasks.named("build"))
}

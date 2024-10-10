import org.gradle.api.JavaVersion

plugins {
  java
  `java-library`

  `maven-publish`

  id("io.papermc.paperweight.userdev") version "1.7.2"
  id("xyz.jpenilla.resource-factory-paper-convention") version "1.1.2"
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
  return kebabCaseString.lowercase().replace(kebabCaseStringSeparator, snakeCaseStringSeparator).replace(" ", snakeCaseStringSeparator)
}

fun pascalCase(kebabCaseString: String): String {
  var pascalCaseString = ""

  val splitString = kebabCaseString.split(kebabCaseStringSeparator)

  for (part in splitString) {
    pascalCaseString += capitalizeFirstLetter(part)
  }

  return pascalCaseString
}

description = "A Minecraft Paper plugin that adds a firework-focused PvP game-mode."

val mainProjectAuthor = "Esoteric Organisation"

val projectAuthors = listOfNotNull(mainProjectAuthor, "Esoteric Enderman", "rolyPolyVole")

val topLevelDomain = "org"
val projectNameString = rootProject.name
val bootstrapperNameString = rootProject.name + "-bootstrapper"

group = topLevelDomain + groupStringSeparator + snakeCase(mainProjectAuthor.lowercase()) + groupStringSeparator + snakeCase(projectNameString)
version = "1.0.0"

val buildDirectoryString = layout.buildDirectory.toString()

val projectGroupString = group.toString()
val projectVersionString = version.toString()

val javaVersion = 21
val javaVersionEnumMember = JavaVersion.valueOf("VERSION_$javaVersion")
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
  paperweight.paperDevBundle("$paperApiVersion-R0.1-SNAPSHOT")

  implementation("dev.jorel" , "commandapi-bukkit-shade-mojang-mapped" , "9.5.1")
  implementation("net.sf", "sevenzipjbinding:sevenzipjbinding", "16.02-2.01")
  implementation("net.sf", "sevenzipjbinding:sevenzipjbinding-all-platforms", "16.02-2.01")
}

tasks {
  build {
    dependsOn(shadowJar)
  }

  shadowJar {
    archiveFileName = "$projectNameString-$projectVersionString.jar"
  }

  compileJava {
    options.release = javaVersion
  }

  javadoc {
    options.encoding = Charsets.UTF_8.name()
  }
}

paperPluginYaml {
  authors = projectAuthors

  main = projectGroupString + groupStringSeparator + pascalCase(projectNameString)
  apiVersion = paperApiVersion
  description = project.description

  bootstrapper = projectGroupString + groupStringSeparator + pascalCase(bootstrapperNameString)
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

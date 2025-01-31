import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.expectale"
version = "1.2.0"

val mojangMapped = project.hasProperty("mojang-mapped")

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.paperweight)
    alias(libs.plugins.nova)
    alias(libs.plugins.stringremapper)
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.xenondevs.xyz/releases")
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper)
    implementation(libs.nova)
}

addon {
    id.set("simple_elevator")
    name.set("Simple-Elevator")
    version.set(project.version.toString())
    novaVersion.set(libs.versions.nova)
    main.set("com.expectale.SimpleElevator")
    authors.add("CptBeffHeart")
}

remapStrings {
    remapGoal.set(if (mojangMapped) "mojang" else "spigot")
    gameVersion.set(libs.versions.paper.get().substringBefore("-"))
}

tasks {
    register<Copy>("addonJar") {
        group = "build"
        if (mojangMapped) {
            dependsOn("jar")
            from(File(buildDir, "libs/${project.name}-${project.version}-dev.jar"))
        } else {
            dependsOn("reobfJar")
            from(File(buildDir, "libs/${project.name}-${project.version}.jar"))
        }
        
        from(File(project.buildDir, "libs/${project.name}-${project.version}.jar"))
        into((project.findProperty("outDir") as? String)?.let(::File) ?: project.buildDir)
        rename { "${addonMetadata.get().addonName.get()}-${project.version}.jar" }
    }
    
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
}
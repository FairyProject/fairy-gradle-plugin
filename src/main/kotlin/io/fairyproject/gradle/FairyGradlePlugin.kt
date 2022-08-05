package io.fairyproject.gradle

import io.fairyproject.gradle.compiler.FairyCompilerAction
import io.fairyproject.gradle.constants.UrlConstants
import io.fairyproject.gradle.dependency.DependencyManagementPlugin
import io.fairyproject.gradle.extension.FairyExtension
import io.fairyproject.gradle.platform.PlatformPlugin
import io.fairyproject.gradle.resource.FairyResourceAction
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.scala.ScalaPlugin
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.jvm.tasks.Jar

class FairyGradlePlugin : Plugin<Project> {

    private lateinit var sourceSets: SourceSetContainer

    override fun apply(project: Project) {
        project.extensions.create("fairy", FairyExtension::class.java)
        this.configureRepositories(project)

        project.plugins.apply(JavaBasePlugin::class.java)
        project.plugins.apply(PlatformPlugin::class.java)
        project.plugins.apply(DependencyManagementPlugin::class.java)

        sourceSets = project.extensions.getByType(JavaPluginExtension::class.java).sourceSets
        project.tasks.withType(Jar::class.java) { configureJarTask(project, it) }
        project.plugins.withType(JavaPlugin::class.java) { configurePlugin(project, "java") }
        project.plugins.withType(GroovyPlugin::class.java) { configurePlugin(project, "groovy") }
        project.plugins.withType(ScalaPlugin::class.java) { configurePlugin(project, "scala") }
        project.plugins.withId("org.jetbrains.kotlin.jvm") { configurePlugin(project, "kotlin") }
    }

    private fun configureJarTask(project: Project, jar: Jar) {
        val action = project.objects.newInstance(FairyResourceAction::class.java)
        jar.doLast("fairyResource", action)
    }

    private fun configurePlugin(project: Project, language: String) {
        sourceSets.all { sourceSet ->
            project.tasks.named(sourceSet.getCompileTaskName(language), AbstractCompile::class.java) {
                val action = project.objects.newInstance(FairyCompilerAction::class.java)
                it.doLast("fairyCompile", action)
            }
        }
    }

    private fun configureRepositories(project: Project) {
        project.repositories.maven { it.setUrl(UrlConstants.repositoryUrl) }
    }

}
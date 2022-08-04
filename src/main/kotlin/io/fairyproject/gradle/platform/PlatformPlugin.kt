package io.fairyproject.gradle.platform

import io.fairyproject.gradle.constants.GradleConstants
import io.fairyproject.gradle.extension.FairyExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class PlatformPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.getByType(FairyExtension::class.java)
        project.afterEvaluate {
            extension.allPlatforms.forEach {
                project.dependencies.add(extension.compile, project.dependencies.create(GradleConstants.DEPENDENCY_FORMAT.format(it.dependencyName)))
            }
        }
    }

}
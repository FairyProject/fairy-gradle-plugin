package io.fairyproject.gradle.platform

import io.fairyproject.gradle.constants.GradleConstants
import io.fairyproject.gradle.dependency.DependencyWriter
import io.fairyproject.gradle.extension.FairyExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class PlatformPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.getByType(FairyExtension::class.java)
        project.afterEvaluate {
            val bootstraps = extension.bootstraps.getOrElse(false)
            val tests = extension.tests.getOrElse(false)
            val version = extension.version.get()

            extension.allPlatforms.forEach {
                val platform = it.dependencyName
                DependencyWriter.write(project, extension, GradleConstants.PLATFORM_FORMAT.format(platform, version))
                if (bootstraps)
                    DependencyWriter.write(project, extension, GradleConstants.BOOTSTRAPS_FORMAT.format(platform, version))
                if (tests)
                    project.dependencies.add("testImplementation", GradleConstants.TESTS_FORMAT.format(platform, version))
            }
        }
    }

}
package io.fairyproject.gradle.dependency

import io.fairyproject.gradle.extension.FairyExtension
import org.gradle.api.Project

object DependencyWriter {

    fun write(project: Project, extension: FairyExtension, dependency: Any) {
        val configurationName = if (extension.compile.getOrElse(false))
            "implementation"
        else
            "compileOnly"
        val tests = extension.tests.getOrElse(false)

        project.dependencies.add(configurationName, dependency)
        if (tests)
            project.dependencies.add("testImplementation", dependency)
    }

}
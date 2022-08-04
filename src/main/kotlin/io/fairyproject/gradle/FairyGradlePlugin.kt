package io.fairyproject.gradle

import io.fairyproject.gradle.dependency.DependencyManagementPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class FairyGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.plugins.apply(DependencyManagementPlugin::class.java)
    }

}
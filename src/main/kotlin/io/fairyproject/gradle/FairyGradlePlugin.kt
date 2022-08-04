package io.fairyproject.gradle

import io.fairyproject.gradle.constants.UrlConstants
import io.fairyproject.gradle.dependency.DependencyManagementPlugin
import io.fairyproject.gradle.extension.FairyExtension
import io.fairyproject.gradle.platform.PlatformPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class FairyGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create("fairy", FairyExtension::class.java)
        this.configureRepositories(project)

        project.plugins.apply(PlatformPlugin::class.java)
        project.plugins.apply(DependencyManagementPlugin::class.java)
    }

    private fun configureRepositories(project: Project) {
        project.repositories.maven { it.setUrl(UrlConstants.repositoryUrl) }
    }

}
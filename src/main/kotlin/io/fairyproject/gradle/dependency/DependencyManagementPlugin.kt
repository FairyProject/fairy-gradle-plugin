package io.fairyproject.gradle.dependency

import io.fairyproject.gradle.constants.UrlConstants
import io.fairyproject.gradle.dependency.bom.Bom
import org.apache.maven.model.Model
import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import org.apache.maven.model.io.xpp3.MavenXpp3Writer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.FileReader
import java.nio.file.Files

class DependencyManagementPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create("dependencyManagement", DependencyManagementExtension::class.java)
        project.afterEvaluate {
            val version = extension.version.get()
            val bom = readBom(project, extension)
            extension.all.forEach { apply(it, version, bom) }
        }
    }

    private fun readBom(project: Project, extension: DependencyManagementExtension): Bom {
        val version = extension.version.get()
        val cacheable = !version.contains("-SNAPSHOT")
        if (cacheable)
            readCacheBom(project, version) ?.let { return it }

        val jsonObject = khttp.get(UrlConstants.bomDetailsUrl.format(version)).jsonObject
        val files = jsonObject.getJSONArray("files")
        files.forEach {
            val json = it as JSONObject
            val name = json.getString("name")
            val contentType = json["contentType"]

            if (contentType == "application/xml") {
                val reader = MavenXpp3Reader()
                val raw = khttp.get(UrlConstants.bomUrl.format(version, name)).raw
                val model = reader.read(raw)

                if (cacheable)
                    writeCacheBom(project, model)
                return Bom(model)
            }
        }
        throw IllegalStateException("Unable to fetch bom from imanity maven repo.")
    }

    private fun readCacheBom(project: Project, version: String): Bom? {
        val file = project.buildDir.resolve("fairy/bom-$version.pom")
        if (file.exists()) {
            val reader = MavenXpp3Reader()
            val model = reader.read(FileReader(file))
            return Bom(model)
        }
        return null
    }

    private fun writeCacheBom(project: Project, model: Model) {
        val file = project.buildDir.resolve("fairy/bom-${model.version}.pom")
        if (file.exists())
            file.delete()

        file.createNewFile()
        val writer = MavenXpp3Writer()
        val outputStream = ByteArrayOutputStream()
        writer.write(outputStream, model)

        file.writeBytes(outputStream.toByteArray())
    }

    private fun apply(module: String, version: String, bom: Bom) {

    }

}
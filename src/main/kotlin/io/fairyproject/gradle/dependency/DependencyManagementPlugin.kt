package io.fairyproject.gradle.dependency

import com.google.gson.Gson
import com.google.gson.JsonObject
import io.fairyproject.gradle.constants.GradleConstants
import io.fairyproject.gradle.constants.UrlConstants
import io.fairyproject.gradle.dependency.bom.Bom
import io.fairyproject.gradle.extension.FairyExtension
import io.fairyproject.gradle.lib.Lib
import io.fairyproject.gradle.lib.libOf
import org.apache.maven.model.Model
import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import org.apache.maven.model.io.xpp3.MavenXpp3Writer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileReader
import java.io.InputStreamReader
import java.util.jar.JarFile

class DependencyManagementPlugin : Plugin<Project> {

    private val gson = Gson()

    override fun apply(project: Project) {
        val extension = project.extensions.getByType(FairyExtension::class.java)
        val configuration = project.configurations.maybeCreate("fairy")
        project.afterEvaluate {
            val version = extension.version.get()
            val bom = readBom(project, extension)

            DependencyWriter.write(
                project,
                extension,
                project.dependencies.platform(GradleConstants.DEPENDENCY_FORMAT.format("bom", version))
            )
            extension.allModules.forEach {
                val resolvedName = DependencyResolver.resolve(it, extension, bom)
                apply(resolvedName, version, project, configuration)
            }

            configuration.dependencies.forEach { dependency ->
                DependencyWriter.write(project, extension, dependency)
            }

            configuration.resolve()
            val libraries = mutableListOf<Lib>()
            val exclusives = mutableListOf<Pair<String, String>>()
            configuration.forEach { readFile(it, libraries, exclusives) }
//            TODO("add libraries and exclusives to compiler task.")
        }
    }

    private fun readFile(
        file: File,
        libraries: MutableList<Lib>,
        exclusives: MutableList<Pair<String, String>>)
    {
        JarFile(file).use { jarFile ->
            jarFile.getEntry("module.json")?.let {
                val jsonObject = gson.fromJson(
                    InputStreamReader(jarFile.getInputStream(it)),
                    JsonObject::class.java
                )
                if (jsonObject.has("exclusives")) {
                    for ((key, value) in jsonObject.getAsJsonObject("exclusives").entrySet()) {
                        exclusives.add(Pair(key, value.asString))
                    }
                }
                if (jsonObject.has("libraries")) {
                    jsonObject.getAsJsonArray("libraries").forEach { element ->
                        libraries.add(libOf(element.asJsonObject))
                    }
                }
            }
        }
    }

    private fun readBom(project: Project, extension: FairyExtension): Bom {
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
                val raw = khttp.get(UrlConstants.bomUrl.format(version, name)).content
                val model = reader.read(ByteArrayInputStream(raw))

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

    private fun apply(module: String, version: String, project: Project, configuration: Configuration) {
        configuration.dependencies.add(project.dependencies.create(GradleConstants.DEPENDENCY_FORMAT.format(module, version)))
    }

}
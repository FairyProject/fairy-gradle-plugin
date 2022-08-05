package io.fairyproject.gradle.resource.impl

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.fairyproject.gradle.dependency.DependencyData
import io.fairyproject.gradle.extension.FairyExtension
import io.fairyproject.gradle.resource.*
import org.gradle.api.Project

open class FairyResourcePluginMeta: FairyResource {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    override fun generate(
        project: Project,
        fairyExtension: FairyExtension,
        classMapper: Map<ClassType, ClassInfo>
    ): ResourceInfo {
        val jsonObject = JsonObject()

        jsonObject.addProperty("name", project.name)
        val classInfo = classMapper[ClassType.MAIN_CLASS]
        if (classInfo != null) {
            jsonObject.addProperty("mainClass", classInfo.name.replace('/', '.'))
        }
        fairyExtension.mainPackage.orNull ?.let {
            jsonObject.addProperty("shadedPackage", it)
        }

        val jsonArray = JsonArray()
        DependencyData.libraries.forEach { lib -> jsonArray.add(lib.jsonObject) }
        jsonObject.add("libraries", jsonArray)

        return resourceOf("fairy.json", gson.toJson(jsonObject).encodeToByteArray())
    }
}
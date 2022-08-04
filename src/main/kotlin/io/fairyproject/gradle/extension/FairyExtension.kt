package io.fairyproject.gradle.extension

import io.fairyproject.gradle.PlatformType
import org.gradle.api.model.ObjectFactory

class FairyExtension(objectFactory: ObjectFactory) {

    val version = objectFactory.property(String::class.java)
    val bootstrap = objectFactory.property(String::class.java)
    val compile = objectFactory.property(Boolean::class.java)
    val allModules : List<String>
        get() = modules
    val allPlatforms : List<PlatformType>
        get() = platforms

    private val platforms = mutableListOf<PlatformType>()
    private val modules = mutableListOf<String>()

    /**
     * Install target module to the project
     */
    fun module(module: String) {
        modules += module
    }

    /**
     * Install target platform to the project
     */
    fun platform(name: String) {
        val platform = PlatformType.values().firstOrNull { it.name.lowercase() == name.lowercase() }
        platform
            ?.let { platforms.add(it) }
            ?: throw IllegalStateException("Platform with name $name does not exists")
    }

}
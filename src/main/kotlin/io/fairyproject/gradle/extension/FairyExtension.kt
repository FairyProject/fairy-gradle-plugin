package io.fairyproject.gradle.extension

import io.fairyproject.gradle.platform.PlatformType
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

open class FairyExtension(objectFactory: ObjectFactory) {

    val version: Property<String> = objectFactory.property(String::class.java)
    val bootstraps: Property<Boolean> = objectFactory.property(Boolean::class.java)
    val compile: Property<Boolean> = objectFactory.property(Boolean::class.java)
    val tests: Property<Boolean> = objectFactory.property(Boolean::class.java)
    val allModules : List<String>
        get() = modules
    val allPlatforms : List<PlatformType>
        get() = platforms

    private val platforms = mutableListOf<PlatformType>()
    private val modules = mutableListOf<String>()

    fun doBootstraps() {
        bootstraps.set(true)
    }

    fun doCompile() {
        compile.set(true)
    }

    fun doTests() {
        tests.set(true)
    }

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
        val platform = PlatformType.VALUES.firstOrNull { it.name.lowercase() == name.lowercase() }
        platform
            ?.let { platforms.add(it) }
            ?: throw IllegalStateException("Platform with name $name does not exists")
    }

}
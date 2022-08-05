package io.fairyproject.gradle.extension

import io.fairyproject.gradle.extension.property.BukkitProperties
import io.fairyproject.gradle.extension.property.Properties
import io.fairyproject.gradle.platform.PlatformType
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

open class FairyExtension(objectFactory: ObjectFactory) {

    val version: Property<String> = objectFactory.property(String::class.java)
    val mainPackage: Property<String> = objectFactory.property(String::class.java)
    val bootstraps: Property<Boolean> = objectFactory.property(Boolean::class.java)
    val compile: Property<Boolean> = objectFactory.property(Boolean::class.java)
    val tests: Property<Boolean> = objectFactory.property(Boolean::class.java)
    val allModules : List<String>
        get() = modules
    val allPlatforms : List<PlatformType>
        get() = platforms

    private val properties = mutableMapOf<PlatformType, Properties>()
    private val platforms = mutableListOf<PlatformType>()
    private val modules = mutableListOf<String>()

    /**
     * Set the fairy version
     */
    fun version(version: String) {
        this.version.set(version)
    }

    /**
     * Install bootstrap to the project
     */
    fun doBootstraps() {
        bootstraps.set(true)
    }

    /**
     * Compile and implement fairy to the project (compileOnly -> implementation)
     */
    fun doCompile() {
        compile.set(true)
    }

    /**
     * Install unit testing tools to the project
     */
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

    /**
     * Get properties for bukkit platform
     */
    fun bukkitProperties(): BukkitProperties = this.properties.computeIfAbsent(PlatformType.BUKKIT) { BukkitProperties() } as BukkitProperties

}
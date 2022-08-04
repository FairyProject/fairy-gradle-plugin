package io.fairyproject.gradle.dependency

import org.gradle.api.model.ObjectFactory


class DependencyManagementExtension(objectFactory: ObjectFactory) {

    val version = objectFactory.property(String::class.java)
    val all : List<String>
        get() = modules
    private val modules = mutableListOf<String>()

    fun module(module: String) {
        modules += module
    }

}
package io.fairyproject.gradle.dependency

import io.fairyproject.gradle.lib.Lib

object DependencyData {

    lateinit var libraries: List<Lib>
    lateinit var exclusives: List<Pair<String, String>>

}
package io.fairyproject.gradle.dependency

import io.fairyproject.gradle.dependency.bom.Bom
import io.fairyproject.gradle.extension.FairyExtension
import io.fairyproject.gradle.platform.PlatformType

object DependencyResolver {

    /**
     * resolve an official dependency name based on input
     * @param input the user input
     * @param extension project fairy extension
     * @param bom the bill of materials read from repository
     * @return dependency name
     * @throws IllegalArgumentException if none of the name can be found
     */
    fun resolve(input: String, extension: FairyExtension, bom: Bom): String {
        if (valid(input, bom))
            // user's original input is already valid
            return input

        // check input with format $platformName-$input for example bukkit-$input
        PlatformType.VALUES
            // if the platform is installed in the game it has higher priority to be checked
            .sortedByDescending { extension.allPlatforms.indexOf(it) }
            .forEach {
                val name = "${it.dependencyName}-$input"
                if (valid(name, bom))
                    return name
            }

        val moduleName = "module.$input"
        if (valid(moduleName, bom))
            return moduleName

        // none of them are valid
        throw IllegalStateException("Unable to resolve valid name by input $input")
    }

    private fun valid(name: String, bom: Bom): Boolean = bom.modules.contains(name.lowercase())

}
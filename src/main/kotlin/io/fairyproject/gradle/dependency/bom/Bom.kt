package io.fairyproject.gradle.dependency.bom

import org.apache.maven.model.Model
import java.util.stream.Collectors

class Bom(model: Model) {

    val version = model.version
    val modules = model.dependencies.stream()
        .map { it.artifactId }
        .collect(Collectors.toSet())

}
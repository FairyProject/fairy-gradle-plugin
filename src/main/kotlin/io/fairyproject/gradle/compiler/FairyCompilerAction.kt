package io.fairyproject.gradle.compiler

import io.fairyproject.gradle.file.ClassModifierCancellable
import io.fairyproject.gradle.file.ClassModifierEvent
import org.gradle.api.Action
import org.gradle.api.Task
import org.gradle.api.tasks.compile.AbstractCompile
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode

open class FairyCompilerAction : Action<Task> {

    private val classModifiers = arrayOf(
        ClassModifierCancellable(),
        ClassModifierEvent()
    )

    override fun execute(t: Task) {
        val compile = t as AbstractCompile
        doPostCompile(compile)
    }

    private fun doPostCompile(compile: AbstractCompile) {
        compile.destinationDirectory.asFileTree
            .filter { it.isFile && it.name.endsWith(".class") }
            .forEach {
                var changed = false
                var bytes = it.readBytes()
                var classReader = ClassReader(bytes)
                var classNode = ClassNode()

                classReader.accept(
                    classNode,
                    ClassReader.SKIP_CODE or ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES
                )

                classModifiers.forEach { classModifier ->
                    classModifier.modify(classNode, classReader) ?.let {
                        changed = true
                        bytes = it
                        classReader = ClassReader(bytes)
                        classNode = ClassNode()
                        classReader.accept(
                            classNode,
                            ClassReader.SKIP_CODE or ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES
                        )
                    }
                }

                if (changed)
                    it.writeBytes(bytes)
            }
    }
}
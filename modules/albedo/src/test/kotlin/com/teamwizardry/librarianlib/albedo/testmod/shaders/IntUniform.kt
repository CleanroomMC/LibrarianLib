package com.teamwizardry.librarianlib.albedo.testmod.shaders

import com.mojang.blaze3d.matrix.MatrixStack
import com.teamwizardry.librarianlib.albedo.GLSL
import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.albedo.testmod.ShaderTest
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.rendering.SimpleRenderTypes
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.util.Identifier
import org.lwjgl.opengl.GL11
import java.awt.Color

internal object IntUniform: ShaderTest<IntUniform.Test>() {

    override fun doDraw(matrixStack: MatrixStack) {
        val minX = 0.0
        val minY = 0.0
        val maxX = 128.0
        val maxY = 128.0

        val c = Color.WHITE

        shader.primitive.set(10)
        shader.vector2.set(10, 20)
        shader.vector3.set(10, 20, 30)
        shader.vector4.set(10, 20, 30, 40)

        val buffer = IRenderTypeBuffer.getImpl(Client.tessellator.buffer)
        val vb = buffer.getBuffer(renderType)

        vb.pos2d(minX, maxY).color(c).tex(0f, 1f).endVertex()
        vb.pos2d(maxX, maxY).color(c).tex(1f, 1f).endVertex()
        vb.pos2d(maxX, minY).color(c).tex(1f, 0f).endVertex()
        vb.pos2d(minX, minY).color(c).tex(0f, 0f).endVertex()

        shader.bind()
        buffer.finish()
        shader.unbind()
    }

    private val renderType = SimpleRenderTypes.flat(Identifier("minecraft:missingno"), GL11.GL_QUADS)

    class Test: Shader("int_tests", null, Identifier("ll-albedo-test:shaders/int_tests.frag")) {
        val primitive = GLSL.glInt()
        val vector2 = GLSL.ivec2()
        val vector3 = GLSL.ivec3()
        val vector4 = GLSL.ivec4()
    }
}
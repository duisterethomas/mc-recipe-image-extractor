package net.bloople.recipeimagesexporter

import com.mojang.blaze3d.platform.GlConst
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.systems.VertexSorter
import net.minecraft.client.MinecraftClient.IS_SYSTEM_MAC
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gl.SimpleFramebuffer
import net.minecraft.client.render.*
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.util.BufferAllocator
import net.minecraft.client.util.ScreenshotRecorder
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.joml.Matrix4f
import org.joml.Matrix4fStack


fun renderToTexture(
    width: Int,
    height: Int,
    r: Float,
    g: Float,
    b: Float,
    scaleFactor: Double,
    block: () -> Unit
): NativeImage {
    RenderSystem.assertOnRenderThread()

    val framebuffer = SimpleFramebuffer(width, height, true, IS_SYSTEM_MAC)
    framebuffer.setClearColor(r, g, b, 0.0f)
    framebuffer.resize(width, height, IS_SYSTEM_MAC)

    try {
        val matrixStack: Matrix4fStack = RenderSystem.getModelViewStack()
        matrixStack.pushMatrix()
        RenderSystem.applyModelViewMatrix()
        RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT or GlConst.GL_COLOR_BUFFER_BIT, IS_SYSTEM_MAC)
        framebuffer.beginWrite(true)
        BackgroundRenderer.clearFog()
        RenderSystem.enableCull()

        RenderSystem.viewport(0, 0, width, height)

        RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT, IS_SYSTEM_MAC)
        val matrix4f = Matrix4f().setOrtho(
            0.0f,
            (width.toDouble() / scaleFactor).toFloat(),
            (height.toDouble() / scaleFactor).toFloat(),
            0.0f,
            1000.0f,
            3000.0f
        )
        RenderSystem.setProjectionMatrix(matrix4f, VertexSorter.BY_DISTANCE)
        val matrixStack2 = RenderSystem.getModelViewStack()
        matrixStack2.identity()
        matrixStack2.translate(0.0f, 0.0f, -2000.0f)
        RenderSystem.applyModelViewMatrix()
        DiffuseLighting.enableGuiDepthLighting()

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShader(GameRenderer::getPositionTexProgram)
        RenderSystem.setShaderTexture(0, Identifier.ofVanilla("textures/gui/widgets.png"))

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()

        block()

        RenderSystem.disableBlend()

        RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT, IS_SYSTEM_MAC)

        framebuffer.endWrite()
        matrixStack.popMatrix()
        matrixStack.pushMatrix()
        RenderSystem.applyModelViewMatrix()
        framebuffer.draw(width, height)
        matrixStack.popMatrix()
        RenderSystem.applyModelViewMatrix()

        return ScreenshotRecorder.takeScreenshot(framebuffer)
    }
    finally {
        framebuffer.delete()
    }
}

fun renderText(textRenderer: TextRenderer, text: Text, x: Int, y: Int, color: Int) {
    val matrixStack = MatrixStack()

    val immediate = VertexConsumerProvider.immediate(BufferAllocator(256))

    textRenderer.draw(
        text,
        x.toFloat(),
        y.toFloat(),
        color,
        false,
        matrixStack.peek().positionMatrix,
        immediate,
        TextRenderer.TextLayerType.NORMAL,
        0,
        LightmapTextureManager.MAX_LIGHT_COORDINATE
    )

    immediate.draw()
}
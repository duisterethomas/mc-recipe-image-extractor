package nl.duisterethomas.recipeimageextractor

import com.mojang.blaze3d.platform.GlConst
import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.platform.NativeImage
import com.mojang.blaze3d.pipeline.TextureTarget
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.VertexSorting
import net.minecraft.client.Minecraft
import net.minecraft.client.Minecraft.ON_OSX
import net.minecraft.client.gui.GuiGraphics
import org.joml.Matrix4f
import org.joml.Matrix4fStack


fun renderToTexture(
    client: Minecraft,
    width: Int,
    height: Int,
    scaleFactor: Double,
    block: (GuiGraphics) -> Unit
): NativeImage {
    RenderSystem.assertOnRenderThread()

    val textureTarget = TextureTarget(width, height, true, ON_OSX)
    textureTarget.setClearColor(0.0f, 0.0f, 0.0f, 0.0f)
    textureTarget.clear(ON_OSX)
    val matrixStack: Matrix4fStack = RenderSystem.getModelViewStack()

    try {
        matrixStack.pushMatrix()

        textureTarget.bindWrite(true)
        RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT or GlConst.GL_COLOR_BUFFER_BIT, ON_OSX)
        RenderSystem.viewport(0, 0, width, height)

        RenderSystem.setProjectionMatrix(
            Matrix4f().setOrtho(
                0.0f,
                (width.toDouble() / scaleFactor).toFloat(),
                (height.toDouble() / scaleFactor).toFloat(),
                0.0f,
                1000.0f,
                3000.0f
            ),
            VertexSorting.DISTANCE_TO_ORIGIN
        )
        matrixStack.identity()
        matrixStack.translate(0.0f, 0.0f, -2000.0f)
        RenderSystem.applyModelViewMatrix()
        Lighting.setupFor3DItems()

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()

        val graphics = GuiGraphics(client, client.renderBuffers().bufferSource())
        block(graphics)
        graphics.flush()

        textureTarget.unbindWrite()

        val image = NativeImage(width, height, false)
        RenderSystem.bindTexture(textureTarget.colorTextureId)
        image.downloadTexture(0, false)
        image.flipY()

        return image
    }
    finally {
        matrixStack.popMatrix()
        RenderSystem.applyModelViewMatrix()
        textureTarget.destroyBuffers()
    }
}
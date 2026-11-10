package nl.duisterethomas.recipeimageextractor

import com.mojang.blaze3d.platform.NativeImage
import net.minecraft.client.Minecraft
import net.minecraft.world.item.ItemStack
import net.minecraft.network.chat.Component
import net.minecraft.core.registries.BuiltInRegistries
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.AlphaComposite
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.awt.image.Raster
import kotlin.reflect.KClass

fun <T> Array<out T>.getOrLast(index: Int): T? {
    if(isEmpty()) return null
    return if (index in 0..lastIndex) get(index) else get(lastIndex)
}

val ItemStack.uniqueKey: String get() = "${BuiltInRegistries.ITEM.getKey(item)}:$count"

fun BufferedImage.asARGB(): BufferedImage {
    if(type == BufferedImage.TYPE_INT_ARGB) return this
    return BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB).apply {
        val graphics = graphics
        graphics.drawImage(this@asARGB, 0, 0, null)
        graphics.dispose()
    }
}

fun BufferedImage.getData(x: Int, y: Int, width: Int, height: Int): Raster {
    return this.getData(Rectangle(x, y, width, height)).createTranslatedChild(0, 0)
}

fun NativeImage.toStraightAlpha(): BufferedImage {
    val premultiplied = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE)
    val pixel = IntArray(1)
    for (y in 0 until height) {
        for (x in 0 until width) {
            val abgr = getPixelRGBA(x, y)
            pixel[0] = (abgr and 0xFF00FF00.toInt()) or
                ((abgr and 0xFF) shl 16) or
                ((abgr ushr 16) and 0xFF)
            premultiplied.raster.setDataElements(x, y, pixel)
        }
    }

    val straight = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val graphics = straight.createGraphics()
    graphics.composite = AlphaComposite.Src
    graphics.drawImage(premultiplied, 0, 0, null)
    graphics.dispose()
    return straight
}

inline fun <R> Graphics2D.use(block: Graphics2D.() -> R): R {
    try {
        return block(this)
    }
    finally {
        dispose()
    }
}

fun Minecraft.sendMessage(message: String) {
    execute {
        gui.chat.addMessage(Component.literal(message))
        narrator.sayChat(Component.literal(message))
    }
}

fun getLogger(name: String): Logger {
    return LoggerFactory.getLogger("$MOD_ID/$name")
}

fun getLogger(clazz: KClass<*>): Logger {
    val fullName = if(clazz.isCompanion) clazz.java.declaringClass.name
    else clazz.qualifiedName

    val name = fullName!!.removePrefix("nl.duisterethomas.recipeimageextractor.")

    return getLogger(name)
}
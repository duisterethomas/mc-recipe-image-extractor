package net.bloople.recipeimagesexporter

import net.minecraft.client.MinecraftClient
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.registry.Registries
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.awt.image.Raster
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*
import kotlin.reflect.KClass

fun <T> Array<out T>.getOrLast(index: Int): T? {
    if(isEmpty()) return null
    return if (index in 0..lastIndex) get(index) else get(lastIndex)
}

val Item.identifier: Identifier get() = Registries.ITEM.getId(this)

val ItemStack.uniqueKey: String get() = "${item.identifier}:$count"

fun decodeBase64(input: String): InputStream {
    return ByteArrayInputStream(Base64.getDecoder().decode(input))
}

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

inline fun <R> Graphics2D.use(block: Graphics2D.() -> R): R {
    try {
        return block(this)
    }
    finally {
        dispose()
    }
}

fun MinecraftClient.sendMessage(message: Text) {
    execute {
        inGameHud.chatHud.addMessage(message)
        narratorManager.narrate(message)
    }
}

fun MinecraftClient.sendMessage(message: String) {
    sendMessage(Text.literal(message))
}

fun getLogger(name: String): Logger {
    return LoggerFactory.getLogger("$MOD_ID/$name")
}

fun getLogger(clazz: KClass<*>): Logger {
    val fullName = if(clazz.isCompanion) clazz.java.declaringClass.name
    else clazz.qualifiedName

    val name = fullName!!.removePrefix("net.bloople.recipeimagesexporter.")

    return getLogger(name)
}
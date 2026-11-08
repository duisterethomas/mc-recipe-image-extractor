package net.bloople.recipeimagesexporter

import net.minecraft.client.Minecraft
import net.minecraft.Util
import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO
import javax.imageio.stream.FileImageOutputStream

class RecipesExporter {
    private lateinit var exportDir: Path

    fun export(client: Minecraft): CompletableFuture<Void> {
        client.sendMessage("Starting export of recipe images")

        val step1 = CompletableFuture.runAsync({
            client.sendMessage("Deleting any existing export")
            exportDir = client.gameDirectory.toPath().toAbsolutePath().resolve("recipe-images-exporter")
            exportDir.toFile().deleteRecursively()
            Files.createDirectories(exportDir)

            client.sendMessage("Deleted any existing export and created export directory $exportDir")
        }, Util.ioPool())

        lateinit var recipeInfos: RecipeInfos
        lateinit var itemIconsExtractor: ItemIconsExtractor
        lateinit var itemLabelsExtractor: ItemLabelsExtractor

        val step2 = step1.thenRunAsync({
            client.sendMessage("Gathering icons and labels")
            recipeInfos = RecipeInfos(client.level!!.recipeManager)
            client.sendMessage("Gathered ${recipeInfos.all.size} recipes for export")
            client.sendMessage("Generating icons and labels")

            itemIconsExtractor = ItemIconsExtractor(
                recipeInfos.itemStacks,
                exportDir,
            )
            itemIconsExtractor.exportIcons(client)

            itemLabelsExtractor = ItemLabelsExtractor(recipeInfos.items, exportDir, client.font)
            itemLabelsExtractor.exportLabels(client)

            client.sendMessage("Generated icons and labels")
        }, client)

        val step3 = step2.thenRunAsync({
            itemIconsExtractor.importIcons()
            itemLabelsExtractor.importLabels()

            val itemsData = ItemsData(
                itemIconsExtractor.icons,
                itemLabelsExtractor.labels,
                itemLabelsExtractor.widths
            )

            for (group in recipeInfos.groups) {
                var count = 0
                for (subGroupChunk in group.subGroups.chunked(100)) {
                    for (subGroup in subGroupChunk) {
                        exportGroup(subGroup, itemsData)
                        count += subGroup.size
                    }
                    client.sendMessage("Exported $count ${group.name} recipes")
                }
            }

            client.sendMessage("Export complete")
        }, Util.ioPool())

        return step3
    }

    private fun exportGroup(group: List<RecipeInfo>, itemsData: ItemsData) {
        for(recipeInfo in group) writeRecipeImage(recipeInfo, recipeInfo.imageGenerator(itemsData).generate())

        if(group.size == 1) return
        val imageGenerators = group.map { it.imageGenerator(itemsData) }
        val maxWidth = imageGenerators.maxOf { it.width }
        val maxHeight = imageGenerators.maxOf { it.height }
        val images = imageGenerators.map { it.generate(maxWidth, maxHeight) }
        writeRecipesAnimation(group[0], images)
    }

    private fun writeRecipeImage(recipeInfo: RecipeInfo, image: BufferedImage) {
        val recipeFilePath = exportDir.resolve("${recipeInfo.recipePath}.png")
        Files.createDirectories(recipeFilePath.parent)
        ImageIO.write(image, "PNG", recipeFilePath.toFile())
    }

    private fun writeRecipesAnimation(firstRecipeInfo: RecipeInfo, images: List<BufferedImage>) {
        val recipeFilePath = exportDir.resolve("${firstRecipeInfo.recipeBasePath}.gif")
        Files.createDirectories(recipeFilePath.parent)
        FileImageOutputStream(recipeFilePath.toFile()).use { outStream ->
            GifSequenceWriter(outStream, images[0].type, 500, true).use {
                for(image in images) it.writeToSequence(image)
            }
        }
    }
}


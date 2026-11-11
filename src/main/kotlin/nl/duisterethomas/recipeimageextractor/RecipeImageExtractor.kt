package nl.duisterethomas.recipeimageextractor

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.api.EnvironmentInterface
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.Minecraft
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.SharedSuggestionProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletionException


const val MOD_ID = "recipeimageextractor"

@EnvironmentInterface(value=EnvType.CLIENT, itf=ClientModInitializer::class)
object RecipeImageExtractor: ClientModInitializer {
    private var exportRunning = false

    @Environment(value=EnvType.CLIENT)
    override fun onInitializeClient() {
        LOGGER.info("onInitializeClient()")

        ClientCommandRegistrationCallback.EVENT.register(ClientCommandRegistrationCallback {
            dispatcher: CommandDispatcher<FabricClientCommandSource?>, _: CommandBuildContext? ->
            dispatcher.register(ClientCommandManager.literal("extract-recipe-images")
                // /extract-recipe-images (no argument, uses the defaults)
                .executes { context: CommandContext<FabricClientCommandSource> ->
                    export(context.source.client)
                    1
                }
                // /extract-recipe-images true|false
                .then(ClientCommandManager.argument("ingredientList", BoolArgumentType.bool())
                    .executes { context: CommandContext<FabricClientCommandSource> ->
                        export(
                            context.source.client,
                            BoolArgumentType.getBool(context, "ingredientList")
                        )
                        1
                    }
                    .then(ClientCommandManager.argument("namespace", StringArgumentType.word())
                        .suggests { context, builder ->
                            val namespaces = context.source.client.level?.recipeManager
                                ?.recipes?.map { it.id.namespace }?.distinct()?.sorted() ?: emptyList()
                            SharedSuggestionProvider.suggest(namespaces, builder)
                        }
                        .executes { context: CommandContext<FabricClientCommandSource> ->
                            export(
                                context.source.client,
                                BoolArgumentType.getBool(context, "ingredientList"),
                                StringArgumentType.getString(context, "namespace")
                            )
                            1
                        }
                    )
                )
            )
        })

        LOGGER.info("end onInitializeClient()")
    }

    private fun export(client: Minecraft, ingredientList: Boolean = true, namespace: String? = null) {
        try {
            if(exportRunning) {
                client.sendMessage("Please wait for the current extraction to complete before starting another one")
                return
            }

            exportRunning = true
            RecipesExporter().export(client, ingredientList, namespace).thenRunAsync { exportRunning = false }.exceptionallyAsync { exception ->
                handleException(client, if(exception is CompletionException) exception.cause else exception)
                return@exceptionallyAsync null
            }
        }
        catch(exception: Exception) {
            handleException(client, exception)
        }
    }

    private fun handleException(client: Minecraft, throwable: Throwable?) {
        LOGGER.error("Caught exception during extraction", throwable)
        exportRunning = false
        client.sendMessage("Extraction failed due to an error, see log for details")
    }

    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)
}
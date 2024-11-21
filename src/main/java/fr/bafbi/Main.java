package fr.bafbi;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import fr.bafbi.blocs.Unbreakable;
import fr.bafbi.commands.SchemCommand;
import fr.bafbi.crafting.CraftingManager;
import fr.bafbi.crafting.TestRecipe;
import fr.bafbi.items.Fireball;
import fr.bafbi.items.GrapplingHook;
import fr.bafbi.blocs.TestBlockHandler;
import fr.bafbi.commands.GamemodeCommand;
import fr.bafbi.miniworld.MiniworldCommand;
import fr.bafbi.timer.SkyTimerManager;
import fr.bafbi.timer.TimerCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.instance.block.BlockManager;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.DeclareRecipesPacket;
import net.minestom.server.recipe.*;
import net.minestom.server.utils.time.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        System.setProperty("minestom.experiment.pose-updates", "true");

        // Set the logging level for fr.bafbi.timer.SkyTimerManager to INFO
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger skyTimerManagerLogger = loggerContext.getLogger("fr.bafbi.timer.SkyTimerManager");
        skyTimerManagerLogger.setLevel(Level.INFO);

        MinecraftServer.setCompressionThreshold(0);

        MinecraftServer minecraftServer = MinecraftServer.init();

        PingHandler.init();

        BlockManager blockManager = MinecraftServer.getBlockManager();
//        blockManager.registerBlockPlacementRule(new DripstonePlacementRule());
        blockManager.registerHandler(TestBlockHandler.INSTANCE.getNamespaceId(), () -> TestBlockHandler.INSTANCE);
        blockManager.registerHandler(Unbreakable.INSTANCE.getNamespaceId(), () -> Unbreakable.INSTANCE);

        CommandManager commandManager = MinecraftServer.getCommandManager();
        commandManager.register(SchemCommand.INSTANCE);
        commandManager.register(new GamemodeCommand());
        commandManager.register(TimerCommand.INSTANCE);
        commandManager.register(MiniworldCommand.INSTANCE);


        RecipeManager recipeManager = MinecraftServer.getRecipeManager();

        Recipe recipe = new TestRecipe();
        recipeManager.addRecipe(recipe);



        commandManager.setUnknownCommandCallback((sender, command) -> sender.sendMessage(Component.text("Unknown command", NamedTextColor.RED)));

        MinecraftServer.getBenchmarkManager().enable(Duration.of(10, TimeUnit.SECOND));

        MinecraftServer.getSchedulerManager().buildShutdownTask(() -> System.out.println("Good night"));


//        var ironBlockRecipe = new ShapedRecipe(
//                "minestom:test", 2, 2, "",
//                RecipeCategory.Crafting.MISC,
//                List.of(
//                        new DeclareRecipesPacket.Ingredient(List.of(ItemStack.of(Material.IRON_INGOT))),
//                        new DeclareRecipesPacket.Ingredient(List.of(ItemStack.of(Material.IRON_INGOT))),
//                        new DeclareRecipesPacket.Ingredient(List.of(ItemStack.of(Material.IRON_INGOT))),
//                        new DeclareRecipesPacket.Ingredient(List.of(ItemStack.of(Material.IRON_INGOT)))
//                ), ItemStack.of(Material.IRON_BLOCK), true) {
//            @Override
//            public boolean shouldShow(@NotNull Player player) {
//                return true;
//            }
//        };
//        MinecraftServer.getRecipeManager().addRecipe(ironBlockRecipe);
//        var recipe = new ShapelessRecipe(
//                "minestom:test2", "abc",
//                RecipeCategory.Crafting.MISC,
//                List.of(
//                        new DeclareRecipesPacket.Ingredient(List.of(ItemStack.of(Material.DIRT)))
//                ),
//                ItemStack.builder(Material.GOLD_BLOCK)
//                        .set(ItemComponent.CUSTOM_NAME, Component.text("abc"))
//                        .build()
//        ) {
//            @Override
//            public boolean shouldShow(@NotNull Player player) {
//                return true;
//            }
//        };
//        MinecraftServer.getRecipeManager().addRecipe(recipe);

        CraftingManager.init();

        PlayerInit.init();

        SkyTimerManager.init();

        EventNode<PlayerEvent> playground = EventNode.type("playground", EventFilter.PLAYER, (event, player) ->
                player.getGameMode().equals(GameMode.ADVENTURE)
        );
        MinecraftServer.getGlobalEventHandler().addChild(playground);
        ItemManager itemManager = new ItemManager(playground);
        itemManager.register(Fireball.INSTANCE);
        itemManager.register(new GrapplingHook());

//        VelocityProxy.enable("abcdef");
        //BungeeCordProxy.enable();

//        MojangAuth.init();

        // useful for testing - we don't need to worry about event calls so just set this to a long time
//        OpenToLAN.open(new OpenToLANConfig().eventCallDelay(Duration.of(1, TimeUnit.DAY)));

        LOGGER.info("Init finished, starting server");
        minecraftServer.start("0.0.0.0", 25565);
//        minecraftServer.start(java.net.UnixDomainSocketAddress.of("minestom-demo.sock"));
        //Runtime.getRuntime().addShutdownHook(new Thread(MinecraftServer::stopCleanly));
    }
}
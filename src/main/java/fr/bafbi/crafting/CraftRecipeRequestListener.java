package fr.bafbi.crafting;

import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.player.PlayerPluginMessageEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.client.common.ClientPluginMessagePacket;
import net.minestom.server.network.packet.client.play.ClientCraftRecipeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CraftRecipeRequestListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CraftRecipeRequestListener.class);

    public static void listener(ClientCraftRecipeRequest packet, Player player) {
       LOGGER.info("Craft recipe request from player {}", player.getUsername());

       player.getInventory().setItemStack(36, ItemStack.of(Material.DIRT));
    }
}

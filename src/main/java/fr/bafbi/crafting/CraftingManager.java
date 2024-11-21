package fr.bafbi.crafting;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientCraftRecipeRequest;

public class CraftingManager {




    public static void init() {

        MinecraftServer.getPacketListenerManager().setPlayListener(ClientCraftRecipeRequest.class, CraftRecipeRequestListener::listener);

    }

}

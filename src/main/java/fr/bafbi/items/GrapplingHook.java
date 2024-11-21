package fr.bafbi.items;

import fr.bafbi.ItemHandler;
import net.minestom.server.collision.Aerodynamics;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

public class GrapplingHook implements ItemHandler {

    private static final NamespaceID NAMESPACE_ID = NamespaceID.from("bafbi:grappling_hook");



    public static ItemStack ItemStack() {
        return ItemStack.builder(Material.FISHING_ROD)
                .amount(1)
                .set(ItemHandler.HANDLER_ID_TAG, NAMESPACE_ID)
                .build();
    }

    @Override
    public void onUse(PlayerUseItemEvent event) {
//        event.setCancelled(true);

        final Player player = event.getPlayer();


        Pos playerPos = player.getPosition();
        Entity entity = new GrapplingHookEntity();
        entity.setInstance(player.getInstance(), playerPos.withY(y -> y + 1.5));
        entity.setLeashHolder(player);
        entity.setInvisible(true);
        entity.setGlowing(true);
        entity.setAerodynamics(new Aerodynamics(0.1f, 1f, 1f));
        Vec velocity = playerPos.direction().mul(20);
        entity.setVelocity(velocity);
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NAMESPACE_ID;
    }
}

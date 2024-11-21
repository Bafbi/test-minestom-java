package fr.bafbi.blocs;

import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

public class TestBlockHandler implements BlockHandler {
    public static final BlockHandler INSTANCE = new TestBlockHandler();

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NamespaceID.from("minestom", "test");
    }

    @Override
    public void onPlace(@NotNull Placement placement) {
//        System.out.println(placement);
    }

    @Override
    public void onDestroy(@NotNull Destroy destroy) {
//        System.out.println(destroy);
        if (destroy instanceof PlayerDestroy playerDestroy) {
            final Player player = playerDestroy.getPlayer();
            player.getInventory().addItemStack(ItemStack.of(Material.OAK_LOG));

        }
    }
}
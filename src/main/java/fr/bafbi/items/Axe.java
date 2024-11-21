package fr.bafbi.items;

import fr.bafbi.ItemHandler;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

public class Axe implements ItemHandler {

    private static final NamespaceID NAMESPACE_ID = NamespaceID.from("bafbi:axe");

    public static ItemStack itemStack() {
        return ItemHandler.withHandler(ItemStack.builder(Material.DIAMOND_AXE)
                .build(), this);
    }
    
    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NAMESPACE_ID;
    }
}

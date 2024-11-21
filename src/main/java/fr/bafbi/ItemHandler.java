package fr.bafbi;

import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import net.minestom.server.event.player.PlayerUseItemEvent;

public interface ItemHandler {

    Tag<NamespaceID> HANDLER_ID_TAG = Tag.String("handler_id").map(NamespaceID::from, NamespaceID::asString);

    default void onUse(PlayerUseItemEvent event) {
    }

    @NotNull
    NamespaceID getNamespaceId();


    public static ItemStack withHandler(ItemStack itemStack, ItemHandler handler) {
        return itemStack.withTag(HANDLER_ID_TAG, handler.getNamespaceId());
    }
}

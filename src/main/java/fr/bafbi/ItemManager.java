package fr.bafbi;

import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.utils.NamespaceID;

import java.util.HashMap;
import java.util.Map;

public class ItemManager {

    private final EventNode< PlayerEvent> eventNode;

    public final Map<NamespaceID, ItemHandler> itemHandlers = new HashMap<>();

    public ItemManager(EventNode<PlayerEvent> eventNode) {
        this.eventNode = eventNode;
        initEventListeners();
    }

    private void initEventListeners() {
        eventNode.addListener(PlayerUseItemEvent.class, event -> {
            var player = event.getPlayer();
            var itemStack = player.getItemInMainHand();
            if (!itemStack.hasTag(ItemHandler.HANDLER_ID_TAG)) return;
            var handlerId = itemStack.getTag(ItemHandler.HANDLER_ID_TAG);

            var itemHandler = itemHandlers.get(handlerId);
            if (itemHandler != null) {
                itemHandler.onUse(event);
            }
        });
    }

    public void register(ItemHandler itemHandler) {
        itemHandlers.put(itemHandler.getNamespaceId(), itemHandler);
    }
}

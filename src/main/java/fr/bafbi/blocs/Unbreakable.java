package fr.bafbi.blocs;

import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Unbreakable implements BlockHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Unbreakable.class);

    private static final NamespaceID NAMESPACE_ID = NamespaceID.from("bafbi", "unbreakable");

    public static final Unbreakable INSTANCE = new Unbreakable();

    @Override
    public void onDestroy(@NotNull BlockHandler.Destroy destroy) {
        if (destroy instanceof PlayerDestroy) {
            LOGGER.error("Player {} brock an unbreakable block at {}", ((PlayerDestroy) destroy).getPlayer().getUsername(), destroy.getBlockPosition());
            return;
        }
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NAMESPACE_ID;
    }
}

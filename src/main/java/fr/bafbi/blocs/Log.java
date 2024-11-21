package fr.bafbi.blocs;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.metadata.item.ItemEntityMeta;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

public class Log implements BlockHandler{

    private final int tier;

    public final Block block = Block.OAK_LOG.withHandler(this);

    public Log(int tier) {
        this.tier = tier;
    }

    @Override
    public void onDestroy(@NotNull BlockHandler.Destroy destroy) {
        var instance = destroy.getInstance();
        var blockPosition = destroy.getBlockPosition();
        var block = instance.getBlock(blockPosition);

        // Drop the block
        var itemEntity = new ItemEntity(ItemStack.of(Material.OAK_LOG));
        itemEntity.setPickupDelay(Duration.ofMillis(100));
        itemEntity.setInstance(instance);
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NamespaceID.from("bafbi:log");
    }
}

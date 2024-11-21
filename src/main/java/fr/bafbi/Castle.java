package fr.bafbi;

import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class Castle {

    public static final Block BLOCK;

    static {
        BLOCK = Block.STONE_BRICKS.withHandler(new Handler());
    }


    public static class Handler implements BlockHandler {
        @Override
        public void onPlace(@NotNull Placement placement) {
            var scheduler = MinecraftServer.getSchedulerManager();
            final var BLOCKS = Arrays.asList(Block.WHITE_WOOL, Block.RED_WOOL, Block.ORANGE_WOOL, Block.YELLOW_WOOL, Block.LIME_WOOL, Block.GREEN_WOOL, Block.LIGHT_BLUE_WOOL, Block.BLUE_WOOL, Block.PURPLE_WOOL, Block.MAGENTA_WOOL, Block.PINK_WOOL, Block.BROWN_WOOL, Block.BLACK_WOOL);
            final int[] i = {0};
            scheduler.scheduleTask(() -> {
                placement.getInstance().setBlock(placement.getBlockPosition(), BLOCKS.get(i[0]));
                i[0] = (i[0] + 1) % BLOCKS.size();
            }, TaskSchedule.seconds(1), TaskSchedule.seconds(1));
        }

        @Override
        public @NotNull NamespaceID getNamespaceId() {
            // Namespace required for serialization purpose
            return NamespaceID.from("bafbi:structure");
        }
    }

}

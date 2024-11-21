package fr.bafbi.miniworld.generators;

import fr.bafbi.blocs.Unbreakable;
import fr.bafbi.schem.SchemsManager;
import fr.bafbi.blocs.TestBlockHandler;
import net.hollowcube.schem.Rotation;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class Floor1 implements Generator {

    public static final DynamicRegistry.Key<DimensionType> DIMENSION_TYPE = MinecraftServer.getDimensionTypeRegistry().register(NamespaceID.from("bafbi", "floor1"),
    DimensionType.builder().build());

    public static final Floor1 GENERATOR = new Floor1();



    @Override
    public void generate(@NotNull GenerationUnit unit) {
        var random = ThreadLocalRandom.current();
        var treeNumber = random.nextInt(1, 3);

        var treeSchem = SchemsManager.loadResourceSchem("tree_9.schem").orElseThrow();


        // Calculate the height of the terrain
        // 0.01\left(x-10\right)^{\ 2}\ +\ 10\ \left\{x>10\right\}
        // \left(x-10\right)^{\ 3}\ +\ 10\ \left\{x<\ 10\right\}
        for (int x = 0; x < unit.size().blockX(); x++) {
            for (int z = 0; z < unit.size().blockZ(); z++) {
                Point currentPos = unit.absoluteStart().add(x, 0, z);

                int distanceToCenter = (int) Math.floor( currentPos.distance(0, currentPos.y(), 0));

                final int height = Math.max(Math.min(distanceToCenter > 10 ? (int) (0.005 * Math.pow(distanceToCenter - 10, 2) + 10) : (int) (0.1 * Math.pow(distanceToCenter - 10, 3) + 10), 200),-50);

                final Point floorPos = currentPos.withY(height - 1);
                final Point underFloorPos = floorPos.withY(y -> y - 10);

                unit.modifier().fill(underFloorPos.withY(y -> y +1), floorPos.add(1,0,1), Block.GRASS_BLOCK);
                unit.modifier().setBlock(underFloorPos, Block.BEDROCK.withHandler(Unbreakable.INSTANCE));

                if (distanceToCenter < 20) continue;
                // Generate trees
                if (random.nextInt(0, unit.size().blockX() * unit.size().blockZ()) > treeNumber) continue;


                unit.fork(setter -> {

                    treeSchem.apply(Rotation.NONE, ((point, block) -> {
                        if (block.isAir()) return;
                        setter.setBlock(floorPos.add(point), block.withHandler(TestBlockHandler.INSTANCE));
                    }));


                });
            }
        }
    }
}

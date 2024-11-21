package fr.bafbi;

import fr.bafbi.items.Fireball;
import fr.bafbi.items.GrapplingHook;
import fr.bafbi.blocs.TestBlockHandler;
import fr.bafbi.schem.SchemsManager;
import fr.bafbi.timer.SkyTimerManager;
import net.hollowcube.schem.Rotation;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.FeatureFlag;
import net.minestom.server.MinecraftServer;
import net.minestom.server.advancements.FrameType;
import net.minestom.server.advancements.notifications.Notification;
import net.minestom.server.advancements.notifications.NotificationCenter;
import net.minestom.server.adventure.MinestomAdventure;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.*;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.event.server.ServerTickMonitorEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.predicate.BlockPredicate;
import net.minestom.server.instance.block.predicate.BlockTypeFilter;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.BlockPredicates;
import net.minestom.server.monitoring.BenchmarkManager;
import net.minestom.server.monitoring.TickMonitor;
import net.minestom.server.network.packet.client.play.ClientCraftRecipeRequest;
import net.minestom.server.network.packet.server.common.CustomReportDetailsPacket;
import net.minestom.server.network.packet.server.common.ServerLinksPacket;
import net.minestom.server.network.packet.server.play.DeclareRecipesPacket;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.utils.MathUtils;
import net.minestom.server.utils.time.TimeUnit;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

public class PlayerInit {

    private static final Inventory inventory;

    private static final Map<Player, Entity> timers = new HashMap<>();

    private static final EventNode<Event> DEMO_NODE = EventNode.all("demo")
            .addListener(EntityAttackEvent.class, event -> {
                final Entity source = event.getEntity();
                final Entity entity = event.getTarget();

                entity.takeKnockback(0.4f, Math.sin(source.getPosition().yaw() * 0.017453292), -Math.cos(source.getPosition().yaw() * 0.017453292));

                if (entity instanceof Player) {
                    Player target = (Player) entity;
                    target.damage(Damage.fromEntity(source, 5));
                }

                if (source instanceof Player) {
                    ((Player) source).sendMessage("You attacked something!");
                }
            })
            .addListener(PlayerDeathEvent.class, event -> event.setChatMessage(Component.text("custom death message")))
            .addListener(PickupItemEvent.class, event -> {
                final Entity entity = event.getLivingEntity();
                if (entity instanceof Player) {
                    // Cancel event if player does not have enough inventory space
                    final ItemStack itemStack = event.getItemEntity().getItemStack();
                    event.setCancelled(!((Player) entity).getInventory().addItemStack(itemStack));
                }
            })
            .addListener(ItemDropEvent.class, event -> {
                final Player player = event.getPlayer();
                ItemStack droppedItem = event.getItemStack();

                Pos playerPos = player.getPosition();
                ItemEntity itemEntity = new ItemEntity(droppedItem);
                itemEntity.setPickupDelay(Duration.of(500, TimeUnit.MILLISECOND));
                itemEntity.setInstance(player.getInstance(), playerPos.withY(y -> y + 1.5));
                Vec velocity = playerPos.direction().mul(6);
                itemEntity.setVelocity(velocity);
            })
            .addListener(PlayerDisconnectEvent.class, event -> System.out.println("DISCONNECTION " + event.getPlayer().getUsername()))
            .addListener(AsyncPlayerConfigurationEvent.class, event -> {
                final Player player = event.getPlayer();

                // Show off adding and removing feature flags
                event.addFeatureFlag(FeatureFlag.BUNDLE);
                event.removeFeatureFlag(FeatureFlag.TRADE_REBALANCE); // not enabled by default, just removed for demonstration

                var instances = MinecraftServer.getInstanceManager().getInstances();
                Instance instance = instances.stream().skip(new Random().nextInt(instances.size())).findFirst().orElse(null);
                event.setSpawningInstance(instance);
                int x = Math.abs(ThreadLocalRandom.current().nextInt()) % 500 - 250;
                int z = Math.abs(ThreadLocalRandom.current().nextInt()) % 500 - 250;
                player.setRespawnPoint(new Pos(0, 40f, 0));
            })
//            .addListener(PlayerHandAnimationEvent.class, event -> {
//                class A {
//                    static boolean b = false;
//                }
//                if (A.b) {
//                    event.getPlayer().getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(NamespaceID.from("test"));
//                } else {
//                    event.getPlayer().getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(new AttributeModifier(NamespaceID.from("test"), 0.5, AttributeOperation.ADD_VALUE));
//                }
//                A.b = !A.b;
//            })

            .addListener(PlayerSpawnEvent.class, event -> {
                final Player player = event.getPlayer();
                player.setGameMode(GameMode.SPECTATOR);
                player.setPermissionLevel(4);
                ItemStack itemStack = ItemStack.builder(Material.STONE)
                        .amount(64)
                        .set(ItemComponent.CAN_PLACE_ON, new BlockPredicates(new BlockPredicate(new BlockTypeFilter.Blocks(Block.STONE), null, null)))
                        .set(ItemComponent.CAN_BREAK, new BlockPredicates(new BlockPredicate(new BlockTypeFilter.Blocks(Block.DIAMOND_ORE), null, null)))
                        .build();
                player.getInventory().addItemStack(itemStack);

                player.sendPacket(new CustomReportDetailsPacket(Map.of(
                        "hello", "world"
                )));

                player.sendPacket(new ServerLinksPacket(
                        new ServerLinksPacket.Entry(ServerLinksPacket.KnownLinkType.NEWS, "https://minestom.net"),
                        new ServerLinksPacket.Entry(ServerLinksPacket.KnownLinkType.BUG_REPORT, "https://minestom.net"),
                        new ServerLinksPacket.Entry(Component.text("Hello world!"), "https://minestom.net")
                ));

                player.getInventory().addItemStack(Fireball.ItemStack());
                player.getInventory().addItemStack(GrapplingHook.ItemStack());

                player.getInventory().addItemStack(ItemStack.builder(Material.GOLDEN_AXE).set(ItemComponent.CAN_BREAK, new BlockPredicates(new BlockPredicate(Block.OAK_LOG))).build());

                if (event.isFirstSpawn()) {
                    Notification notification = new Notification(
                            Component.text("Welcome!"),
                            FrameType.TASK,
                            Material.IRON_SWORD
                    );
//                    NotificationCenter.send(notification, event.getPlayer());

                    player.playSound(Sound.sound(SoundEvent.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.Source.PLAYER, 0.5f, 1f));
                }

//                player.sendPackets(MinecraftServer.getRecipeManager().getDeclareRecipesPacket());


                SkyTimerManager.sendTimerToPlayer(player, Duration.ofSeconds(10));


            })
//            .addListener(PlayerMoveEvent.class, playerMoveEvent -> {
//                final Player player = playerMoveEvent.getPlayer();
//                final Entity entity = timers.get(player);
//                if (entity != null) {
//                    entity.refreshPosition(player.getPosition().withView(0,0).withY(y -> y + 50));
//                }
//            })

            .addListener(ServerTickMonitorEvent.class, event -> {
                timers.values().forEach(entity -> {
                    entity.editEntityMeta(TextDisplayMeta.class, meta -> {
                        var now = LocalDateTime.now();
                        meta.setText(Component.text(now.getHour() + ":" + now.getMinute() + ":" + now.getSecond()).color(NamedTextColor.BLACK));
                    });
                });
            })

            .addListener(PlayerPacketOutEvent.class, event -> {
                var name = event.getPacket().getClass().getSimpleName();
                if (!name.contains("Recipe")) return;
                System.out.println("out " + event.getPacket().getClass().getSimpleName());
//                DeclareRecipesPacket packet = (DeclareRecipesPacket) event.getPacket();
//                System.out.println(packet.recipes().size());
//                packet.recipes().forEach(recipe -> {
//                    System.out.println(recipe.getClass().getSimpleName());
//                });
            })

            .addListener(PlayerUseItemOnBlockEvent.class, event -> {
                if (event.getHand() != Player.Hand.MAIN) return;

                var itemStack = event.getItemStack();
                var block = event.getInstance().getBlock(event.getPosition());

                if ("false".equals(block.getProperty("waterlogged")) && itemStack.material().equals(Material.WATER_BUCKET)) {
                    block = block.withProperty("waterlogged", "true");
                } else if ("true".equals(block.getProperty("waterlogged")) && itemStack.material().equals(Material.BUCKET)) {
                    block = block.withProperty("waterlogged", "false");
                } else return;

                event.getInstance().setBlock(event.getPosition(), block);

            })
            .addListener(PlayerBlockPlaceEvent.class, event -> {
//                event.setDoBlockUpdates(false);
            })
            .addListener(PlayerBlockInteractEvent.class, event -> {
                var block = event.getBlock();
                var rawOpenProp = block.getProperty("open");
                if (rawOpenProp == null) return;

                block = block.withProperty("open", String.valueOf(!Boolean.parseBoolean(rawOpenProp)));
                event.getInstance().setBlock(event.getBlockPosition(), block);
            });

    static {
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();

        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        instanceContainer.setGenerator(unit -> {
            var random = ThreadLocalRandom.current();
            var treeNumber = random.nextInt(1, 3);

            var treeSchem = SchemsManager.loadSchem("tree_9.schem").orElseThrow();

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    Point blockPosition = unit.absoluteStart().add(x, 0, z);
                    int distance = (int) Math.floor( blockPosition.distance(0, blockPosition.y(), 0));

                    // Get the height by doing the exponential of the distance
                    final int height = Math.min((int) Math.exp(distance * 0.01) + 10, 256);
//                    if (unit.absoluteStart().blockY() > height && unit.absoluteStart().blockY() + unit.size().blockY() < height) return;

                    final Point floor = blockPosition.withY(height - 1);
                    unit.modifier().fill(blockPosition, floor.add(1,0,1), Block.GRASS_BLOCK);

                    // Generate trees
                    if (random.nextInt(0, unit.size().blockX() * unit.size().blockZ()) > treeNumber) continue;


                    unit.fork(setter -> {


//
//                        setter.setBlock(blockPosition.withY(height), Block.OAK_LOG);
//                        setter.setBlock(blockPosition.withY(height + 1), Block.OAK_LEAVES);
//                        setter.setBlock(blockPosition.withY(height + 2), Block.OAK_LEAVES);
//                        setter.setBlock(blockPosition.withY(height + 1).add(1, 0, 0), Block.OAK_LEAVES);
//                        setter.setBlock(blockPosition.withY(height + 1).add(0, 0, 1), Block.OAK_LEAVES);
//                        setter.setBlock(blockPosition.withY(height + 1).add(-1, 0, 0), Block.OAK_LEAVES);
//                        setter.setBlock(blockPosition.withY(height + 1).add(0, 0, -1), Block.OAK_LEAVES);


                        treeSchem.apply(Rotation.NONE, ((point, block) -> {
                            if (block.isAir()) return;
                            setter.setBlock(floor.add(point.sub(0,1,0)), block.withHandler(TestBlockHandler.INSTANCE));
                        }));


                    });
                }
            }
        });
        instanceContainer.setChunkSupplier(LightingChunk::new);
        instanceContainer.setTimeRate(0);
        instanceContainer.setTime(18000);




//        var i2 = new InstanceContainer(UUID.randomUUID(), DimensionType.OVERWORLD, null, NamespaceID.from("minestom:demo"));
//        instanceManager.registerInstance(i2);
//        i2.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));
//        i2.setChunkSupplier(LightingChunk::new);

        // System.out.println("start");
        // var chunks = new ArrayList<CompletableFuture<Chunk>>();
        // ChunkUtils.forChunksInRange(0, 0, 32, (x, z) -> chunks.add(instanceContainer.loadChunk(x, z)));

        // CompletableFuture.runAsync(() -> {
        //     CompletableFuture.allOf(chunks.toArray(CompletableFuture[]::new)).join();
        //     System.out.println("load end");
        //     LightingChunk.relight(instanceContainer, instanceContainer.getChunks());
        //     System.out.println("light end");
        // });

        inventory = new Inventory(InventoryType.CHEST_1_ROW, Component.text("Test inventory"));
        inventory.setItemStack(3, ItemStack.of(Material.DIAMOND, 34));
    }

    private static final AtomicReference<TickMonitor> LAST_TICK = new AtomicReference<>();

    public static void init() {
        var eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addChild(DEMO_NODE);

        MinestomAdventure.AUTOMATIC_COMPONENT_TRANSLATION = true;
        MinestomAdventure.COMPONENT_TRANSLATOR = (c, l) -> c;

        eventHandler.addListener(ServerTickMonitorEvent.class, event -> LAST_TICK.set(event.getTickMonitor()));

        BenchmarkManager benchmarkManager = MinecraftServer.getBenchmarkManager();
        MinecraftServer.getSchedulerManager().buildTask(() -> {
            if (LAST_TICK.get() == null || MinecraftServer.getConnectionManager().getOnlinePlayerCount() == 0)
                return;

            long ramUsage = benchmarkManager.getUsedMemory();
            ramUsage /= 1e6; // bytes to MB

            TickMonitor tickMonitor = LAST_TICK.get();
            final Component header = Component.text("RAM USAGE: " + ramUsage + " MB")
                    .append(Component.newline())
                    .append(Component.text("TICK TIME: " + MathUtils.round(tickMonitor.getTickTime(), 2) + "ms"))
                    .append(Component.newline())
                    .append(Component.text("ACQ TIME: " + MathUtils.round(tickMonitor.getAcquisitionTime(), 2) + "ms"));
            final Component footer = benchmarkManager.getCpuMonitoringMessage();
            Audiences.players().sendPlayerListHeaderAndFooter(header, footer);
        }).repeat(10, TimeUnit.SERVER_TICK).schedule();
    }
}
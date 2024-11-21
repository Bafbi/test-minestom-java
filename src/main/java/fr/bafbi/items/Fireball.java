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
import net.minestom.server.network.packet.server.play.SetCooldownPacket;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fireball implements ItemHandler {

    public static final Fireball INSTANCE = new Fireball();

    private static final Duration COOLDOWN = Duration.ofMillis(10);
    private static final NamespaceID NAMESPACE_ID = NamespaceID.from("bafbi:fireball");

    private static final Map<Player, Long> lastUsed = new HashMap<>();
    private static final List<Entity> entities = new ArrayList<>();

    static {
//        MinecraftServer.getSchedulerManager().buildTask(() -> {
//            entities.removeIf(Entity::isRemoved);
//            entities.forEach(entity -> {
//                Instance instance = entity.getInstance();
//                Pos pos = entity.getPosition().add(0, -1, 0);
//                if (!instance.getBlock(pos.blockX(), pos.blockY(), pos.blockZ()).isAir()){
//                    entity.remove();
//                    ItemEntity itemEntity = new ItemEntity(ItemStack.of(Material.FIRE_CHARGE));
//                    itemEntity.setInstance(instance, entity.getPosition());
//                    itemEntity.setPickupDelay(Duration.ofSeconds(1));
//                    Vec velocity = entity.getPosition().direction();
//                    itemEntity.setVelocity(velocity);
//                }
//            });
//        }).repeat(1, TimeUnit.SERVER_TICK).schedule();
    }

    public static ItemStack ItemStack() {
        return ItemStack.builder(Material.FIRE_CHARGE)
                .amount(1)
                .set(ItemHandler.HANDLER_ID_TAG, NAMESPACE_ID)
                .build();
    }

    @Override
    public void onUse(PlayerUseItemEvent event) {
        event.setCancelled(true);
        final Player player = event.getPlayer();
        final long currentTime = System.currentTimeMillis();
        final long lastTime = lastUsed.getOrDefault(player, 0L);
        if (currentTime - lastTime < COOLDOWN.toMillis()) {
            return;
        }
        final ItemStack itemStack = event.getItemStack();

        lastUsed.put(player, currentTime);
        player.sendPacket(new SetCooldownPacket(itemStack.material().id(), (int) (COOLDOWN.toSeconds() * 20)));


        Pos playerPos = player.getPosition();
        Entity entity = new FireBallEntity();
        entity.setInstance(player.getInstance(), playerPos.withY(y -> y + 1.5));
        entity.setNoGravity(true);
        entity.setAerodynamics(new Aerodynamics(0f, 1f, 1f));
        Vec velocity = playerPos.direction().mul(30).add(player.getVelocity());
        entity.setVelocity(velocity);
        entities.add(entity);
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NAMESPACE_ID;
    }
}

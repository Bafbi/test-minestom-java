package fr.bafbi.timer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntitySpawnType;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.network.packet.server.play.SetPassengersPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * The sky timer is the timer that every player can see in the sky
 * for that we need to link a TextEntity to the player
 * We want to manage the timer with packet because we don't want the server to manage the TextEntity
 */
public class SkyTimerManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkyTimerManager.class);

    private static final TimerEntity FAKE_ENTITY = new TimerEntity(Duration.ZERO);
    private static final Map<Player, Duration> PLAYERS = new HashMap<>();
    private static long lastUpdate = System.currentTimeMillis();





    /**
     * For the player to see the timer
     * we need to spawn the TextEntity
     * then set it as a passenger of the player
     * and update the metadata of the TextEntity
     * @param player
     */
    public static void sendTimerToPlayer(Player player, Duration time) {

        if (PLAYERS.containsKey(player)) {
            PLAYERS.replace(player, time);
            return;
        }
        FAKE_ENTITY.updateTimer(time);
        FAKE_ENTITY.spawnToPlayer(player);
        PLAYERS.put(player, time);

    }


    public static void init() {
        LOGGER.trace("SkyTimerManager init");
        MinecraftServer.getSchedulerManager().buildTask(() -> {
            var passedTimeMillis = System.currentTimeMillis() - lastUpdate;
            LOGGER.debug("Passed time: {}", passedTimeMillis);
            PLAYERS.replaceAll((player, time) -> time.minusMillis(passedTimeMillis));
            PLAYERS.values().removeIf(Duration::isNegative);
            PLAYERS.forEach((player, time) -> {
                LOGGER.debug("Updating timer for player {} with {}", player.getUsername(), time);
                FAKE_ENTITY.updateTimer(time);
                player.sendPacket(FAKE_ENTITY.getMetadataPacket());
            });
            lastUpdate = System.currentTimeMillis();
        }).repeat(Duration.ofSeconds(1)).schedule();
    }




    static class TimerEntity extends Entity {
        TimerEntity(Duration time) {
            super(EntityType.TEXT_DISPLAY, UUID.fromString("00000000-0000-0000-0000-000000000000"));
            this.editEntityMeta(TextDisplayMeta.class, meta -> {
                meta.setText(getTimerText(time));
                meta.setLeftRotation(new float[]{1, 0, 0, 1});
                meta.setRightRotation(new float[]{0, 0, -1, 1});
                meta.setTranslation(new Vec(-0.5, 50, 0));
                meta.setBackgroundColor(0);
                meta.setHasNoGravity(true);
            });
        }

        /**
         * Spawning the timer should break the passenger system of the player
         * @param player
         */
        public void spawnToPlayer(Player player) {
            var spawnPacket = EntitySpawnType.BASE.getSpawnPacket(this);
            var metadataPacket = this.getMetadataPacket();
            var passengerPacket = new SetPassengersPacket(player.getEntityId(), List.of(this.getEntityId()));

            player.sendPackets(spawnPacket, metadataPacket, passengerPacket);
        }

        public void updateTimer(Duration time) {
            this.editEntityMeta(TextDisplayMeta.class, meta -> {
                meta.setText(getTimerText(time));
            });
        }

        private Component getTimerText(Duration time) {
            if (time.getSeconds() < 1) {
                return Component.text("00:00:00").decorate(TextDecoration.OBFUSCATED).color(NamedTextColor.BLACK);
            }
            if (time.toHours() > 24) {
                return Component.text(time.toHours()).decorate(TextDecoration.OBFUSCATED).color(NamedTextColor.BLACK);
            }
            LocalTime localTime = LocalTime.ofSecondOfDay(time.getSeconds());
            LOGGER.debug("Local time: {}", localTime);
            return Component.text(localTime.format(DateTimeFormatter.ISO_TIME)).color(NamedTextColor.BLACK);
        }
    }





}

package fr.bafbi.miniworld;

import fr.bafbi.miniworld.generators.Floor1;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MiniworldManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(MiniworldManager.class);

    private static final Map<NamespaceID, Instance> MINIWORLDS = new HashMap<>();

    private static final InstanceManager INSTANCE_MANAGER = MinecraftServer.getInstanceManager();


    public static void create(@NotNull NamespaceID namespaceID) {
        if (MINIWORLDS.containsKey(namespaceID)) {
            LOGGER.error("MiniWorld {} already exists", namespaceID);
            return;
        }
        Instance instance = INSTANCE_MANAGER.createInstanceContainer(Floor1.DIMENSION_TYPE);
        instance.setGenerator(Floor1.GENERATOR);
        MINIWORLDS.put(namespaceID, instance);
    }

    public static NamespaceID create() {
        var randomNamespaceID = NamespaceID.from("bafbi", UUID.randomUUID().toString());
        create(randomNamespaceID);
        return randomNamespaceID;
    }

    public static void sendPlayer(@NotNull NamespaceID namespaceID, @NotNull Player player) {
        if (!MINIWORLDS.containsKey(namespaceID)) {
            LOGGER.error("MiniWorld {} not found", namespaceID);
            return;
        }
        var instance = MINIWORLDS.get(namespaceID);
        player.setInstance(instance);
    }

}

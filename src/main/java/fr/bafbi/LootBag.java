package fr.bafbi;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class LootBag implements ItemHandler {

    public static ItemStack ItemStack(LootTable lootTable) {
        System.out.println("Creating lootbag");
        return ItemStack.builder(Material.BUNDLE)

                .customName(Component.text("LootBag", NamedTextColor.GOLD))
                .lore(Component.text("Right click to open", NamedTextColor.GRAY))

                .set(ItemComponent.BUNDLE_CONTENTS, List.of(
                        ItemStack.of(Material.DIAMOND, 5)
                ))
                .set(ItemHandler.HANDLER_ID_TAG, NamespaceID.from("bafbi:loopbag"))
                .set(LootTable.LOOT_TABLE_TAG, lootTable)
                .build();
    }

    private ItemStack itemAnim(ItemStack itemStack, Material[] materials) {
        var scheduler = MinecraftServer.getSchedulerManager();
        final int[] i = {0};
        scheduler.scheduleTask(() -> {
            itemStack.withMaterial(materials[i[0]]);
            i[0] = (i[0] + 1) % materials.length;
        }, TaskSchedule.seconds(1), TaskSchedule.seconds(1));
        return itemStack;
    }

    @Override
    public void onUse(PlayerUseItemEvent event) {
        event.setCancelled(true);
        var lootTable = event.getItemStack().getTag(LootTable.LOOT_TABLE_TAG);

        // Create the inventory
//        ContainerInventory inventory = new Inventory(InventoryType.WINDOW_3X3, "Reward");
//
//        inventory.setItemStack(5, lootTable.getRandomItemStack().orElse(ItemStack.AIR));
//
//        inventory.addInventoryCondition((player, slot, clickType, inventoryConditionResult) -> {
//            player.sendMessage("click type inventory: " + clickType + " slot: " + slot);
//            inventoryConditionResult.setCancel(true);
//
//        });
//
//// Open the inventory for the player
//// (Opening the same inventory for multiple players would result in a shared interface)
//        event.getPlayer().openInventory(inventory);
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NamespaceID.from("bafbi:loopbag");
    }

}

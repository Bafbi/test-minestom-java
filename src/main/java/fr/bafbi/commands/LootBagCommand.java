package fr.bafbi.commands;

import fr.bafbi.LootBag;
import fr.bafbi.LootTable;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static fr.bafbi.commands.LootBagCommand.Subcommand.REMOVE;

public class LootBagCommand extends Command {
    public LootBagCommand() {
        super("lootbag", "lb");

        setDefaultExecutor((sender, context) -> {

            if (!(sender instanceof Player player)) return;


            var lootTable = LootTable.builder()
                    .addItemStack(ItemStack.of(Material.DIAMOND, 2), 3)
                    .addItemStack(ItemStack.of(Material.IRON_INGOT, 5), 1)
                    .build();
            player.getInventory().addItemStack(LootBag.ItemStack(lootTable));


        });

        addSyntax(this::giveSyntax, ArgumentType.Enum("subcommand", Subcommand.class), ArgumentType.Entity("player"));
    }

    enum Subcommand {
        GIVE,
        REMOVE
    }

    private void giveSyntax(CommandSender sender, CommandContext context) {
        if (!context.has("subcommand")) return;
        if (!context.has("player")) return;

        System.out.println(context.get("player").toString());
        if ((context.get("player") instanceof Player player)) {


            switch (context.get("subcommand")) {
                case Subcommand.GIVE -> {
                    sender.sendMessage("Give");
                    var lootTable = LootTable.builder()
                            .addItemStack(ItemStack.of(Material.DIAMOND, 2), 3)
                            .addItemStack(ItemStack.of(Material.IRON_INGOT, 5), 1)
                            .build();
                    player.getInventory().addItemStack(LootBag.ItemStack(lootTable));
                }
                case REMOVE -> {
                    sender.sendMessage("Remove");
                }
                default -> {
                }
            }
        }

        sender.sendMessage("Usage: /lootbag <give|remove> <player>");
    }
}

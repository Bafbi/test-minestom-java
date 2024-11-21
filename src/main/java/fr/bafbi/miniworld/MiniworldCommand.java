package fr.bafbi.miniworld;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

public class MiniworldCommand extends Command {

    public static final MiniworldCommand INSTANCE = new MiniworldCommand();

    MiniworldCommand() {
        super("miniworld", "mw");
        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(Component.text("Usage: /miniworld <create|delete|list|go|create-and-go>", NamedTextColor.RED));
        });

        addSubcommand(CreateAndGoMiniworldSubcommand.INSTANCE);


    }

    static class CreateAndGoMiniworldSubcommand extends Command {
        public static final CreateAndGoMiniworldSubcommand INSTANCE = new CreateAndGoMiniworldSubcommand();

        CreateAndGoMiniworldSubcommand() {
            super("create-and-go");
            setDefaultExecutor((sender, context) -> {
                sender.sendMessage(Component.text("Usage: /miniworld create-and-go", NamedTextColor.GOLD));
            });

            addSyntax((sender, context) -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(Component.text("You must be a player to use this command", NamedTextColor.RED));
                    return;
                }
                MiniworldManager.sendPlayer(MiniworldManager.create(), player);
            });
        }
    }

}

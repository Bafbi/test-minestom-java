package fr.bafbi.schem;

import net.hollowcube.schem.Rotation;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SchemCommand extends Command {

    public SchemCommand() {
        super("schem");


        var pathArgument = ArgumentType.String("path").setSuggestionCallback((sender, context, suggestion) -> {
            for (String schem : SchemsManager.getSchems()) {
                suggestion.addEntry(new SuggestionEntry(schem));
            }
        });

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("Usage: /schem <path>");
        });

        addSyntax(this::loadSchem, pathArgument);

    }

    private void loadSchem(@NotNull CommandSender sender, @NotNull CommandContext context) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be a player to use this command");
            return;
        }
        String path = context.get("path");
        sender.sendMessage("Loading schem: " + path);
        var schem = SchemsManager.loadSchem(path);
        schem.ifPresent(schematic -> {
            schematic.build(Rotation.NONE, false).apply(player.getInstance(), player.getPosition(), () -> {
                player.sendMessage("Done!");
            });
        });


    }
}

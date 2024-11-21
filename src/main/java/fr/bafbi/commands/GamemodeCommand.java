package fr.bafbi.commands;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;

public class GamemodeCommand extends Command {

    public GamemodeCommand() {
        super("gamemode");


        var gamemodeArgument = ArgumentType.String("gamemode").setSuggestionCallback((sender, context, suggestion) -> {
            for (GameMode value : GameMode.values()) {
                suggestion.addEntry(new SuggestionEntry(value.name().toLowerCase()));
            }
        }).map(value -> {
            try {
                return GameMode.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        });

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("Usage: /gamemode <gamemode>");
        });

        addSyntax((sender, context) -> {
            if (!(sender instanceof Player player)) return;
            player.setGameMode(context.get(gamemodeArgument));
        }, gamemodeArgument);

    }

}

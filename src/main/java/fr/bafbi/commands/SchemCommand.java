package fr.bafbi.commands;

import fr.bafbi.schem.SchemsManager;
import net.hollowcube.schem.Rotation;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemCommand extends Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemCommand.class);

    private static final ArgumentString pathArgument = (ArgumentString) ArgumentType.String("path").setSuggestionCallback((sender, context, suggestion) -> {
        for (String schem : SchemsManager.getSchems()) {
            suggestion.addEntry(new SuggestionEntry(schem));
        }
    });

    public static final SchemCommand INSTANCE = new SchemCommand();

    SchemCommand() {
        super("schem");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("Usage: /schem <path>");
        });

        addSyntax((sender, context) -> {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("You must be a player to use this command");
                return;
            }
            String path = context.get(pathArgument);
            LOGGER.info("Loading schem: {}", path);
            var schem = SchemsManager.loadSchem(path);
            schem.ifPresent(schematic -> {
                schematic.build(Rotation.NONE, false).apply(player.getInstance(), player.getPosition(), () -> {
                    sender.sendMessage("Done!");
                });
            });
        }, pathArgument);

        addSyntax((sender, context) -> {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("You must be a player to use this command");
                return;
            }
            String path = context.get(pathArgument);
            LOGGER.info("Loading resource schem: {}", path);
            var schem = SchemsManager.loadResourceSchem(path);
            schem.ifPresent(schematic -> {
                schematic.build(Rotation.NONE, false).apply(player.getInstance(), player.getPosition(), () -> {
                    sender.sendMessage("Done!");
                });
            });
        }, ArgumentType.Literal("resource"), ArgumentType.String("path"));
    }

}

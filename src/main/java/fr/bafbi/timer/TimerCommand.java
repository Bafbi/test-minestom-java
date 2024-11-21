package fr.bafbi.timer;

import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentTime;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimerCommand.class);

    private static final ArgumentTime timeArgument = ArgumentType.Time("time");
    private static final ArgumentEntity playerArgument = (ArgumentEntity) ArgumentType.Entity("start").onlyPlayers(true).setDefaultValue(sender -> {
        if (sender instanceof Player player) {
            return new EntityFinder().setUuid(player.getUuid(), EntityFinder.ToggleableType.INCLUDE);
        }
        return null;
    });

    public static final TimerCommand INSTANCE = new TimerCommand();

    TimerCommand() {
        super("timer");
        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(Component.text("Usage: /timer <set|toggle>", NamedTextColor.RED));
        });

        addSubcommand(SetTimerSubcommand.INSTANCE);
        addSubcommand(ToggleTimerSubcommand.INSTANCE);

    }

    static class SetTimerSubcommand extends Command {

        public static final SetTimerSubcommand INSTANCE = new SetTimerSubcommand();

        SetTimerSubcommand() {
            super("set");
            setDefaultExecutor((sender, context) -> {
                sender.sendMessage(Component.text("Usage: /timer set <time> [player]", NamedTextColor.GOLD));
            });

            addSyntax((sender, context) -> {
                var time = context.get(timeArgument);
                var player = context.get(playerArgument).findFirstPlayer(sender);
                if (player == null) {
                    sender.sendMessage(Component.text("Player not found", NamedTextColor.RED));
                    return;
                }
                // Start the timer with the given time
                LOGGER.info("{} set timer for player {} to {}", sender.get(Identity.NAME).orElse("Unknown"), player.getUsername(), time);
                SkyTimerManager.sendTimerToPlayer(player, time);
            }, timeArgument, playerArgument);


        }

    }

    static class ToggleTimerSubcommand extends Command {

        public static final ToggleTimerSubcommand INSTANCE = new ToggleTimerSubcommand();

        ToggleTimerSubcommand() {
            super("toggle");
            setDefaultExecutor((sender, context) -> {
                sender.sendMessage(Component.text("Usage: /timer toggle [player]", NamedTextColor.GOLD));
            });

            addSyntax((sender, context) -> {
                var player = context.get(playerArgument).findFirstPlayer(sender);
                // Toggle the timer for the given player
                LOGGER.info("{} toggled timer for player {}", sender.identity().examinableName(), player.getUsername());
            }, playerArgument);
        }

    }

}

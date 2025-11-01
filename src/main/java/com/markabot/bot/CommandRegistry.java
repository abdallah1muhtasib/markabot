package com.markabot.bot;

import com.markabot.bot.commands.BaseSlashCommand;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

/**
 * Keeps track of all slash commands and dispatches events to the appropriate handler.
 */
public class CommandRegistry extends ListenerAdapter {

    private final Map<String, BaseSlashCommand> commands = new LinkedHashMap<>();
    private final Consumer<String> infoLogger;
    private final BiConsumer<String, Throwable> errorLogger;

    public CommandRegistry(Consumer<String> infoLogger, BiConsumer<String, Throwable> errorLogger) {
        this.infoLogger = Objects.requireNonNull(infoLogger, "infoLogger");
        this.errorLogger = Objects.requireNonNull(errorLogger, "errorLogger");
    }

    public void register(BaseSlashCommand command) {
        Objects.requireNonNull(command, "command");
        BaseSlashCommand previous = commands.put(command.name(), command);
        if (previous != null) {
            throw new IllegalStateException("Slash command already registered: /" + command.name());
        }
        command.onRegister(this);
        infoLogger.accept("Registered /" + command.name());
    }

    public Collection<BaseSlashCommand> getCommands() {
        return Collections.unmodifiableCollection(commands.values());
    }

    public List<CommandData> createDefinitions() {
        return commands.values().stream()
                .map(BaseSlashCommand::commandData)
                .collect(Collectors.toList());
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        BaseSlashCommand command = commands.get(event.getName());
        if (command == null) {
            event.reply("❔ Unknown command. Please try again in a minute.").setEphemeral(true).queue();
            infoLogger.accept("Received unknown slash command: /" + event.getName());
            return;
        }

        try {
            command.execute(event);
        } catch (Exception ex) {
            errorLogger.accept("Command /" + command.name() + " failed", ex);
            if (!event.isAcknowledged()) {
                event.reply("❌ An error occurred while executing this command. The developer has been notified.")
                        .setEphemeral(true)
                        .queue();
            }
        }
    }
}

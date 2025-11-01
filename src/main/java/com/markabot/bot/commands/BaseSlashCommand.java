package com.markabot.bot.commands;

import com.markabot.bot.CommandRegistry;
import java.util.Objects;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

/**
 * Base contract for all MarkaBot slash commands.
 * <p>
 * Extend this class and override {@link #commandData()} and {@link #execute(SlashCommandInteractionEvent)}
 * to provide the command metadata and behaviour respectively.
 * </p>
 */
public abstract class BaseSlashCommand {

    private final String name;

    protected BaseSlashCommand(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    /**
     * @return The primary slash-command name. Example: {@code "about"} for {@code /about}.
     */
    public final String name() {
        return name;
    }

    /**
     * Builds a fresh {@link CommandData} instance describing this command. The result is used for
     * guild and global upserts, so the returned object should be a new instance each call.
     */
    public abstract CommandData commandData();

    /**
     * Handles the incoming slash command interaction.
     */
    public abstract void execute(SlashCommandInteractionEvent event) throws Exception;

    /**
     * Lifecycle hook invoked once the command is registered inside the {@link CommandRegistry}.
     * Can be overridden to perform setup that requires the registry reference.
     */
    public void onRegister(CommandRegistry registry) {
        // Default no-op
    }
}

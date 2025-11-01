package com.markabot.bot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface BaseSlashCommand {
    String name();
    CommandData definition();
    void handle(SlashCommandInteractionEvent e) throws Exception;
}

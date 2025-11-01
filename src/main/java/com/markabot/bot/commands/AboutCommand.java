package com.markabot.bot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class AboutCommand implements BaseSlashCommand {
    @Override public String name() { return "about"; }
    @Override public CommandData definition() { return Commands.slash(name(), "Show bot status"); }
    @Override public void handle(SlashCommandInteractionEvent e) {
        e.reply("Marka Bot is online ✅").setEphemeral(true).queue();
    }
}

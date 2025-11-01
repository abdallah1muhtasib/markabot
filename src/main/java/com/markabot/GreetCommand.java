package com.markabot.bot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.*;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

public class GreetCommand implements BaseSlashCommand {
    @Override public String name() { return "greet"; }

    @Override public CommandData definition() {
        return Commands.slash(name(), "Manage greeting")
            .addSubcommands(
                new SubcommandData("on",  "Enable greet"),
                new SubcommandData("off", "Disable greet"),
                new SubcommandData("set", "Set greet message")
                    .addOption(STRING, "text", "Message with {user} {guild} {memberCount}", true)
            );
    }

    @Override public void handle(SlashCommandInteractionEvent e) {
        switch (e.getSubcommandName()) {
            case "on"  -> e.reply("✅ Greet enabled.").setEphemeral(true).queue();
            case "off" -> e.reply("❌ Greet disabled.").setEphemeral(true).queue();
            case "set" -> {
                var text = e.getOption("text").getAsString();
                e.reply("Greet message set to:\n> " + text).setEphemeral(true).queue();
            }
            default    -> e.reply("Unknown subcommand").setEphemeral(true).queue();
        }
    }
}

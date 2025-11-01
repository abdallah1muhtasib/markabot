package com.markabot.bot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class GreetCommand extends BaseSlashCommand {

    public GreetCommand() {
        super("greet");
    }

    @Override
    public CommandData commandData() {
        return Commands.slash(name(), "Configure greeting messages")
                .addSubcommands(
                        new SubcommandData("on", "Enable greeting messages"),
                        new SubcommandData("off", "Disable greeting messages"),
                        new SubcommandData("preview", "Preview the current greeting"),
                        new SubcommandData("set", "Set a custom greeting message")
                                .addOption(OptionType.STRING, "text", "Use placeholders like {user} and {guild}", true)
                );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String subcommand = event.getSubcommandName();
        if (subcommand == null) {
            event.reply("❔ Please choose a subcommand.").setEphemeral(true).queue();
            return;
        }

        switch (subcommand) {
            case "on" -> event.reply("✅ Greeting messages have been enabled.").setEphemeral(true).queue();
            case "off" -> event.reply("🚫 Greeting messages have been disabled.").setEphemeral(true).queue();
            case "preview" -> event.reply("👋 Example: Welcome {user} to {guild}! We now have {memberCount} members.")
                    .setEphemeral(true)
                    .queue();
            case "set" -> {
                String text = event.getOption("text") != null ? event.getOption("text").getAsString() : null;
                if (text == null || text.isBlank()) {
                    event.reply("⚠️ Please provide a greeting message.").setEphemeral(true).queue();
                    return;
                }
                event.reply("✍️ Greeting message saved:\n> " + text)
                        .setEphemeral(true)
                        .queue();
            }
            default -> event.reply("❌ Unknown subcommand.").setEphemeral(true).queue();
        }
    }
}

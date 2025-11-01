package com.markabot.bot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class AboutCommand extends BaseSlashCommand {

    public AboutCommand() {
        super("about");
    }

    @Override
    public CommandData commandData() {
        return Commands.slash(name(), "Show MarkaBot information");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("MarkaBot")
                .setDescription("Modern moderation and automation tools for your Discord community.")
                .addField("Guild", guild != null ? guild.getName() : "Direct Message", true)
                .addField("Requested by", event.getUser().getAsTag(), true)
                .setFooter("MarkaBot v1.0")
                .setColor(0x5865F2);

        event.replyEmbeds(embed.build())
                .setEphemeral(true)
                .queue();
    }
}

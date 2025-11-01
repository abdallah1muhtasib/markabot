package com.markabot.bot;

import com.markabot.bot.commands.BaseSlashCommand;
import net.dv8tion.jda.api.JDA;
import java.util.*;

public class CommandRegistry {
    private final Map<String, BaseSlashCommand> commands = new HashMap<>();

    public void register(BaseSlashCommand cmd) { commands.put(cmd.name(), cmd); }
    public Optional<BaseSlashCommand> get(String name) { return Optional.ofNullable(commands.get(name)); }

    /** يسجّل كل تعريفات Slash عالميًا. */
    public void upsertAll(JDA jda) {
        var defs = commands.values().stream().map(BaseSlashCommand::definition).toList();
        jda.updateCommands().addCommands(defs).queue();
    }
}

package com.markabot;

import com.markabot.bot.CommandRegistry;
import com.markabot.bot.SecurityGuards;
import com.markabot.bot.commands.AboutCommand;
import com.markabot.bot.commands.AccessCommand;
import com.markabot.bot.commands.GreetCommand;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public final class BotBootstrap {

    private static final AtomicReference<TextChannel> LOG_CHANNEL = new AtomicReference<>();
    private static volatile String logChannelId;

    private BotBootstrap() {
    }

    public static void main(String[] args) throws Exception {
        System.out.println("🚀 MarkaBot is starting...");

        try {
            Map<String, String> env = System.getenv();
            String token = trimToNull(env.get("BOT_TOKEN"));
            String ownerId = trimToNull(env.get("OWNER_ID"));
            String devGuildId = trimToNull(env.get("DEV_GUILD_ID"));
            logChannelId = trimToNull(env.get("LOG_CHANNEL_ID"));

            if (token == null) {
                System.err.println("❌ BOT_TOKEN is missing! Set it via environment variables.");
                return;
            }

            SecurityGuards securityGuards = new SecurityGuards(ownerId);

            Consumer<String> infoLogger = BotBootstrap::logInfo;
            BiConsumer<String, Throwable> errorLogger = BotBootstrap::logError;

            CommandRegistry registry = new CommandRegistry(infoLogger, errorLogger);
            registry.register(new AboutCommand());
            registry.register(new GreetCommand());
            registry.register(new AccessCommand(securityGuards));
            // Register new slash commands here by instantiating and passing them to registry.register(...)

            JDABuilder builder = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .addEventListeners(registry, new ReadyListener(devGuildId, registry));

            JDA jda = builder.build();
            jda.awaitReady();

            logInfo("✅ MarkaBot is now online!");

            if (logChannelId != null) {
                TextChannel logChannel = jda.getTextChannelById(logChannelId);
                if (logChannel != null) {
                    LOG_CHANNEL.set(logChannel);
                    logChannel.sendMessage("✅ **MarkaBot** is now online at " + OffsetDateTime.now() + "!").queue();
                } else {
                    logInfo("⚠️ Log channel not found. Check LOG_CHANNEL_ID or bot permissions.");
                }
            }
        } catch (Exception exception) {
            logError("Fatal error while starting MarkaBot", exception);
            throw exception;
        }
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static void logInfo(String message) {
        System.out.println(message);
        sendToDiscord("ℹ️ " + message);
    }

    private static void logError(String message, Throwable throwable) {
        System.err.println(message);
        if (throwable != null) {
            throwable.printStackTrace();
        }
        sendToDiscord("❗ " + message);
    }

    private static void sendToDiscord(String message) {
        TextChannel channel = LOG_CHANNEL.get();
        if (channel != null) {
            channel.sendMessage(message).queue(
                    success -> {
                    },
                    error -> {
                        System.err.println("Failed to send log message to Discord: " + error.getMessage());
                        error.printStackTrace();
                    }
            );
        }
    }

    private static final class ReadyListener extends ListenerAdapter {

        private final String devGuildId;
        private final CommandRegistry registry;

        private ReadyListener(String devGuildId, CommandRegistry registry) {
            this.devGuildId = devGuildId;
            this.registry = Objects.requireNonNull(registry, "registry");
        }

        @Override
        public void onReady(ReadyEvent event) {
            upsertGuildCommands(event.getJDA());
            upsertGlobalCommands(event.getJDA());
        }

        private void upsertGuildCommands(JDA jda) {
            if (devGuildId == null) {
                return;
            }
            Guild guild = jda.getGuildById(devGuildId);
            if (guild == null) {
                logInfo("⚠️ DEV_GUILD_ID is set but the guild is not accessible.");
                return;
            }
            List<CommandData> definitions = registry.createDefinitions();
            guild.updateCommands().addCommands(definitions).queue(
                    commands -> logInfo("⚡ Guild slash commands upserted for " + guild.getName()),
                    error -> logError("Failed to upsert guild slash commands", error)
            );
        }

        private void upsertGlobalCommands(JDA jda) {
            List<CommandData> definitions = registry.createDefinitions();
            jda.updateCommands().addCommands(definitions).queue(
                    commands -> logInfo("🌍 Global slash commands upserted"),
                    error -> logError("Failed to upsert global slash commands", error)
            );
        }
    }
}

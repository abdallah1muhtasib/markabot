package com.markabot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class BotBootstrap {
    public static void main(String[] args) throws Exception {
        System.out.println("🚀 MarkaBot is starting...");

        // 1) قراءة التوكن
        String token = System.getenv("BOT_TOKEN");
        if (token == null || token.isEmpty()) {
            System.out.println("❌ BOT_TOKEN is missing! Please set it in Railway Variables.");
            return;
        }

        // 2) بناء JDA
        JDA jda = JDABuilder.createDefault(token)
                .enableIntents(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT
                )
                .build();

        jda.awaitReady();
        System.out.println("✅ MarkaBot is now online!");

        // 3) قراءة LOG_CHANNEL_ID وإرسال رسالة تأكيد
        String logChannelId = System.getenv("LOG_CHANNEL_ID");
        if (logChannelId != null && !logChannelId.isEmpty()) {
            TextChannel logChannel = jda.getTextChannelById(logChannelId);
            if (logChannel != null) {
                logChannel.sendMessage("✅ **MarkaBot** بدأ العمل الآن!").queue();
            } else {
                System.out.println("⚠️ LOG_CHANNEL_ID غير صحيح أو القناة غير موجودة.");
            }
        } else {
            System.out.println("ℹ️ لم يتم ضبط LOG_CHANNEL_ID؛ لن يتم إرسال رسائل اللوق.");
        }

        // مثال بسيط: تقدر ترسل لوقاتك بهذا الشكل لاحقًا:
        // log("تم تسجيل حدث X", jda);
    }

    // دالة مساعدة للّوق (يمكنك استخدامها من أي مكان لاحقاً)
    public static void log(String message, JDA jda) {
        String logChannelId = "1433859021563101405";
        if (logChannelId != null && !logChannelId.isEmpty()) {
            TextChannel logChannel = jda.getTextChannelById(logChannelId);
            if (logChannel != null) {
                logChannel.sendMessage("📝 " + message).queue();
            }
        }
        // إضافة طباعة للكونسول أيضاً
        System.out.println("[LOG] " + message);
    }
}


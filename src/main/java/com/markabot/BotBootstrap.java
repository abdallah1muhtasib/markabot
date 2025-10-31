package com.markabot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class BotBootstrap {
    public static void main(String[] args) throws Exception {
        System.out.println("🚀 MarkaBot is starting...");

        // قراءة التوكن من متغير البيئة في Railway
        String token = System.getenv("BOT_TOKEN");
        if (token == null || token.isEmpty()) {
            System.out.println("❌ BOT_TOKEN is missing! Please set it in Railway Variables.");
            return;
        }

        // إنشاء الـ JDA وربطه بالتوكن
        JDA jda = JDABuilder.createDefault(token)
                .enableIntents(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT
                )
                .build();

        jda.awaitReady(); // انتظار الاتصال قبل المتابعة
        System.out.println("✅ MarkaBot is now online!");
    }
}

package org.nxx5xxx.javaBot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class MusicBot {
    public static void main(String[] args) {
        // 봇 토큰을 여기에 입력하세요
        TokenManager tokenManager = new TokenManager();
        String token = tokenManager.getDiscordBotToken();
        //String token = "YOUR_BOT_TOKEN_HERE";

        try {
            JDA jda = JDABuilder.createDefault(token)
                    // 필요한 권한들
                    .enableIntents(GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.GUILD_VOICE_STATES,
                            GatewayIntent.MESSAGE_CONTENT)
                    // 봇 상태 설정
                    .setActivity(Activity.listening("음악 🎵"))
                    // 이벤트 리스너 추가
                    .addEventListeners(new MusicCommand())
                    .build();

            System.out.println("음악봇이 성공적으로 시작되었습니다!");

        } catch (Exception e) {
            System.err.println("봇 시작 중 오류 발생: " + e.getMessage());
        }
    }
}
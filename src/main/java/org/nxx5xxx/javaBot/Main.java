package org.nxx5xxx.javaBot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

//TIP 코드를 <b>실행</b>하려면 <shortcut actionId="Run"/>을(를) 누르거나
// 에디터 여백에 있는 <icon src="AllIcons.Actions.Execute"/> 아이콘을 클릭하세요.
public class Main {
    public static void main(String[] args) {
        TokenManager tokenManager = new TokenManager();
        String token = tokenManager.getDiscordBotToken();
        JDABuilder.createDefault(token).build();

        /*
        JDABuilder.createDefault(token).setActivity(Activity.competing("")),build;
        Activity.competing(String name)
        ~에 참가 중
        Activity.playing(String name)
        ~하는 중
        Activity.listening(String name)
        ~듣는 중
        Activity.streaming(String name, String url)
        ~방송 중 (해당링크 클릭가능)
        Activity.watching(String name)
        ~ 시청 중
        Activity.customStatus(String name)
        ~~~~~ (스트링 내에것 표기됨)
         */
    }
}
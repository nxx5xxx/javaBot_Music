package org.nxx5xxx.javaBot;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;
import java.util.List;

public class Slash extends ListenerAdapter {
    /*
    onSlashCommandInteraction   유저가 /명령어를 입력했을 때	        명령어에 반응하여 기능 수행
    onGuildReady                봇이 길드(서버)에 정상 연결됐을 때	    슬래시 커맨드 등록, 초기화 작업
     */
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()){
            case "test3":
                event.reply("Test3").queue();
                break;
            case "test4":
                event.reply("Test4").queue();
                break;
        }
//        if (event.getName().equals("ping")) {
//            event.reply("Pong!").queue();
//        }
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
//        event.getGuild().upsertCommand("ping", "핑 테스트").queue();
        List<CommandData> cD = new ArrayList<>();
        cD.add(Commands.slash("test3","test3"));
        cD.add(Commands.slash("test5","test4555"));
        System.out.println(event.getJDA().getGuilds().size());
        event.getGuild().updateCommands().addCommands(cD).queue();
        /*
        여기서 처음 나오는 부분이 onSlashCommandInteraction reply안의 값과 연결됨
         */
    }
}

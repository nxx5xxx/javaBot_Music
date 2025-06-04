package org.nxx5xxx.javaBot;

import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.nxx5xxx.javaBot.music.PlayerManager;

public class Chat extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //onMessageReceived는 수신 받았을때 호출되는 메서드

        String msg = event.getMessage().getContentRaw();
        //event.getMessage().getContentDisplay()
        /*
        event.getMessage().getContentRaw();
        event.getMessage().getContentDisplay()
        event.getMessage().getContentStripped()
        의 차이점은
        예시로 <@1234567890> **굵은 텍스트** <#4567891230> 가 있으면
        실제로는 @홍길동 굵은 텍스트 #일반채널 이렇게 보이나
        각 메서드에 따라 아래처럼 보이게 된다
        getContentRaw()       <@1234567890> **굵은 텍스트** <#4567891230>
        getContentDisplay()   @홍길동 굵은 텍스트 #일반채널
        getContentStripped()  홍길동 굵은 텍스트 일반채널
         */
        System.out.println(msg);
        /*
        User user = event.getAuthor();
        System.out.println("ID: " + user.getId());
        System.out.println("Username: " + user.getName());
        System.out.println("Avatar ID: " + user.getAvatarId()); // null 가능성 있음
        System.out.println("Avatar URL: " + user.getAvatarUrl()); // null 가능성 있음
        System.out.println("Effective Avatar URL: " + user.getEffectiveAvatarUrl()); // 항상 값 있음!
         */
        System.out.println(event.getAuthor().getAvatarId());
        String[] parts = msg.split(" ",2);
        /*
        if(msg.equals("test")){
           event.getChannel().sendMessage("테스트가성공적으로 이루어졌습니다").queue();
        }else if(msg.equals("test2")){
            event.getMessage().reply("답글테스트").queue();
        }else if(parts[0].equals("노래")){
            event.getMessage().reply("노래 테스트").queue();
            playMusic(event,parts[1]);
            event.getMessage().reply(parts[1]).queue();
        }
         */
        switch(parts[0]) {
            case "ping" :
            case "핑" :
                event.getChannel().sendMessage("Pong!").queue();
                break;

            case "대답" :
            case "reply" :
                event.getMessage().reply("Reply!").queue();
                break;

            case "노래" :
            case "play" :
                playMusic(event, parts[1]);
                break;
        }
    }

    public void playMusic(MessageReceivedEvent event,String part){
        System.out.println(part);
        if(!event.getMember().getVoiceState().inAudioChannel()){
            System.out.println(part+"4");
            event.getChannel().sendMessage("음성 채널에 접속하지 않았습니다").queue();
            return;
        }

        if(!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()){
            System.out.println(part+"5");
            final AudioManager audioManager = event.getGuild().getAudioManager();
            final VoiceChannel voiceChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();
            System.out.println(part+"  6  "+voiceChannel);
            audioManager.openAudioConnection(voiceChannel);
        }
        System.out.println(part+"2");
        String link = "ytsearch: " + part + " 노래";
        //String link = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";  // 진짜 URL
//        String link = "ytsearch1:" + part;
        System.out.println(part+"3");
        PlayerManager.getINSTANCE().loadAndPlay(event.getChannel().asTextChannel(), link, event.getMember());
        //재생상태확인
        new Thread(() -> {
            try {
                Thread.sleep(2000); // 2초 후 상태 확인
                var musicManager = PlayerManager.getINSTANCE().getMusicManager(event.getGuild());
                var currentTrack = musicManager.audioPlayer.getPlayingTrack();

                if (currentTrack != null) {
                    System.out.println("현재 재생 중: " + currentTrack.getInfo().title);
                    System.out.println("재생 위치: " + currentTrack.getPosition() + "ms");
                    System.out.println("일시정지 여부: " + musicManager.audioPlayer.isPaused());
                } else {
                    System.out.println("재생 중인 트랙 없음!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }



}

package org.nxx5xxx.javaBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.HashMap;
import java.util.Map;

public class MusicCommand extends ListenerAdapter {
    // 서버별 음악 매니저 저장
    private final Map<Long, MusicManager> musicManagers = new HashMap<>();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // 봇 자신의 메시지는 무시
        if (event.getAuthor().isBot()) return;

        String message = event.getMessage().getContentRaw();
        String[] args = message.split(" ");
        String command = args[0].toLowerCase();

        // 명령어 처리
        switch (command) {
            case "!play":
            case "!재생":
                handlePlayCommand(event, args);
                break;

            case "!stop":
            case "!정지":
                handleStopCommand(event);
                break;

            case "!skip":
            case "!스킵":
                handleSkipCommand(event);
                break;

            case "!queue":
            case "!대기열":
                handleQueueCommand(event);
                break;

            case "!help":
            case "!도움말":
                handleHelpCommand(event);
                break;
        }
    }

    private void handlePlayCommand(MessageReceivedEvent event, String[] args) {
        Member member = event.getMember();
        if (member == null) return;

        // 사용자가 음성채널에 있는지 확인
        VoiceChannel voiceChannel = member.getVoiceState().getChannel().asVoiceChannel();
        if (voiceChannel == null) {
            event.getChannel().sendMessage("❌ 먼저 음성채널에 참여해주세요!").queue();
            return;
        }

        if (args.length < 2) {
            event.getChannel().sendMessage("❌ 사용법: !play <YouTube URL 또는 검색어>").queue();
            return;
        }

        // URL 또는 검색어 추출
        String query = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));

        // 음성채널 연결
        connectToVoiceChannel(event.getGuild(), voiceChannel);

        // 음악 재생 시작
        MusicManager musicManager = getMusicManager(event.getGuild());
        musicManager.loadAndPlay(event.getChannel().asTextChannel(), query);
    }

    private void handleStopCommand(MessageReceivedEvent event) {
        MusicManager musicManager = getMusicManager(event.getGuild());
        musicManager.scheduler.stop();

        // 음성채널에서 나가기
        event.getGuild().getAudioManager().closeAudioConnection();

        event.getChannel().sendMessage("⏹️ 음악을 정지하고 음성채널에서 나갔습니다.").queue();
    }

    private void handleSkipCommand(MessageReceivedEvent event) {
        MusicManager musicManager = getMusicManager(event.getGuild());
        musicManager.scheduler.nextTrack();

        event.getChannel().sendMessage("⏭️ 다음 곡으로 건너뛰었습니다.").queue();
    }

    private void handleQueueCommand(MessageReceivedEvent event) {
        MusicManager musicManager = getMusicManager(event.getGuild());
        String queueInfo = musicManager.scheduler.getQueueInfo();

        event.getChannel().sendMessage("📋 **재생 대기열:**\n" + queueInfo).queue();
    }

    private void handleHelpCommand(MessageReceivedEvent event) {
        String helpMessage = """
                🎵 **음악봇 명령어**
                
                `!play <URL/검색어>` - 음악 재생
                `!stop` - 재생 정지 및 봇 나가기
                `!skip` - 다음 곡으로 건너뛰기
                `!queue` - 재생 대기열 확인
                `!help` - 도움말 표시
                
                💡 **사용 팁:**
                - YouTube URL이나 제목으로 검색 가능
                - 먼저 음성채널에 참여해주세요
                """;

        event.getChannel().sendMessage(helpMessage).queue();
    }

    private void connectToVoiceChannel(Guild guild, VoiceChannel voiceChannel) {
        AudioManager audioManager = guild.getAudioManager();
        if (!audioManager.isConnected()) {
            audioManager.openAudioConnection(voiceChannel);
        }
    }

    private MusicManager getMusicManager(Guild guild) {
        return musicManagers.computeIfAbsent(guild.getIdLong(), guildId -> {
            MusicManager musicManager = new MusicManager();
            guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
            return musicManager;
        });
    }
}
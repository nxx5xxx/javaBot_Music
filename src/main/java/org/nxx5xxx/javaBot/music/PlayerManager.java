package org.nxx5xxx.javaBot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {
    private static PlayerManager INSTANCE;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    private PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        // 오디오 설정 최적화
        audioPlayerManager.getConfiguration().setOpusEncodingQuality(AudioConfiguration.OPUS_QUALITY_MAX);
        audioPlayerManager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);
        audioPlayerManager.getConfiguration().setFilterHotSwapEnabled(true);

        // YouTube 소스 매니저 설정 개선
        try {
            YoutubeAudioSourceManager youtube = new YoutubeAudioSourceManager(
                    true,    // allowSearch
                    true,    // allowDirectVideoIds
                    true     // allowDirectPlaylistIds
            );

            // 추가 설정
            youtube.setPlaylistPageCount(1); // 플레이리스트 페이지 제한

            this.audioPlayerManager.registerSourceManager(youtube);
            System.out.println("YouTube 소스 매니저 등록 완료");

        } catch (Exception e) {
            System.err.println("YouTube 소스 매니저 등록 실패: " + e.getMessage());
            e.printStackTrace();
        }

        // 다른 소스들도 등록
        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);

        System.out.println("PlayerManager 초기화 완료");
    }

    public static PlayerManager getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }

    public void loadAndPlay(TextChannel textChannel, String trackURL, Member client) {
        final GuildMusicManager musicManager = this.getMusicManager(textChannel.getGuild());

        System.out.println("트랙 로딩 시작: " + trackURL);

        this.audioPlayerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                System.out.println("단일 트랙 로드 성공: " + audioTrack.getInfo().title);

                // 트랙 정보 상세 출력
                System.out.println("- 길이: " + audioTrack.getDuration() + "ms");
                System.out.println("- URL: " + audioTrack.getInfo().uri);
                System.out.println("- 스트림 가능: " + audioTrack.isSeekable());

                musicManager.scheduler.queue(audioTrack);
                textChannel.sendMessageFormat("🎵 재생 중인 곡: `%s` (by `%s`)",
                        audioTrack.getInfo().title,
                        audioTrack.getInfo().author
                ).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                System.out.println("플레이리스트 로드 성공 - 곡 수: " + audioPlaylist.getTracks().size());

                if (audioPlaylist.getTracks().isEmpty()) {
                    textChannel.sendMessage("❌ 검색 결과가 없습니다.").queue();
                    return;
                }

                // 첫 번째 트랙 선택
                AudioTrack firstTrack = audioPlaylist.getSelectedTrack() != null
                        ? audioPlaylist.getSelectedTrack()
                        : audioPlaylist.getTracks().get(0);

                System.out.println("선택된 트랙: " + firstTrack.getInfo().title);
                System.out.println("- 길이: " + firstTrack.getDuration() + "ms");
                System.out.println("- URL: " + firstTrack.getInfo().uri);
                System.out.println("- 스트림 가능: " + firstTrack.isSeekable());

                // 트랙이 유효한지 확인
                if (firstTrack.getDuration() == 0) {
                    System.out.println("⚠️ 트랙 길이가 0 - 스트리밍 불가능할 수 있음");
                }

                musicManager.scheduler.queue(firstTrack);

                textChannel.sendMessageFormat(
                        "🎵 재생 중인 곡: `%s` (by `%s`)",
                        firstTrack.getInfo().title,
                        firstTrack.getInfo().author
                ).queue();
            }

            @Override
            public void noMatches() {
                System.out.println("검색 결과 없음: " + trackURL);
                textChannel.sendMessage("❌ 일치하는 결과가 없습니다: `" + trackURL + "`").queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                System.err.println("로드 실패: " + e.getMessage());
                System.err.println("원인: " + e.getCause());
                System.err.println("심각도: " + e.severity);
                e.printStackTrace();

                textChannel.sendMessage("❌ 로드 실패: " + e.getMessage()).queue();

                // 대안 시도
                if (trackURL.contains("ytsearch:")) {
                    String searchTerm = trackURL.replace("ytsearch:", "");
                    String alternativeUrl = "https://www.youtube.com/results?search_query=" + searchTerm.replace(" ", "+");
                    System.out.println("대안 URL 시도: " + alternativeUrl);
                }
            }
        });
    }
}
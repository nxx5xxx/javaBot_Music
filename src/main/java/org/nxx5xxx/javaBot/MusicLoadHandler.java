package org.nxx5xxx.javaBot;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class MusicLoadHandler implements AudioLoadResultHandler {
    private final TextChannel channel;
    private final TrackScheduler scheduler;

    public MusicLoadHandler(TextChannel channel, TrackScheduler scheduler) {
        this.channel = channel;
        this.scheduler = scheduler;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        // 단일 트랙이 로드되었을 때
        scheduler.queue(track);

        String message = "🎵 **재생 대기열에 추가됨:**\n" +
                track.getInfo().title +
                " (" + formatTime(track.getDuration()) + ")";

        channel.sendMessage(message).queue();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        // 플레이리스트가 로드되었을 때
        if (playlist.isSearchResult()) {
            // 검색 결과인 경우 첫 번째 결과만 재생
            AudioTrack firstTrack = playlist.getSelectedTrack();

            if (firstTrack == null) {
                firstTrack = playlist.getTracks().get(0);
            }

            scheduler.queue(firstTrack);

            String message = "🔍 **검색 결과:**\n" +
                    firstTrack.getInfo().title +
                    " (" + formatTime(firstTrack.getDuration()) + ")";

            channel.sendMessage(message).queue();

        } else {
            // 실제 플레이리스트인 경우 모든 트랙 추가
            int tracksAdded = 0;
            for (AudioTrack track : playlist.getTracks()) {
                scheduler.queue(track);
                tracksAdded++;

                // 너무 많은 트랙 추가 방지
                if (tracksAdded >= 50) {
                    break;
                }
            }

            String message = "📋 **플레이리스트 추가됨:**\n" +
                    playlist.getName() +
                    " (" + tracksAdded + "개 트랙)";

            channel.sendMessage(message).queue();
        }
    }

    @Override
    public void noMatches() {
        // 검색 결과가 없을 때
        channel.sendMessage("❌ 검색 결과를 찾을 수 없습니다.").queue();
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        // 로드 실패 시
        String errorMessage = "❌ 음악 로드 실패: ";

        if (exception.getMessage().contains("age-restricted")) {
            errorMessage += "연령 제한된 콘텐츠입니다.";
        } else if (exception.getMessage().contains("region blocked")) {
            errorMessage += "지역 차단된 콘텐츠입니다.";
        } else if (exception.getMessage().contains("not available")) {
            errorMessage += "사용할 수 없는 콘텐츠입니다.";
        } else {
            errorMessage += exception.getMessage();
        }

        channel.sendMessage(errorMessage).queue();
        System.err.println("음악 로드 실패: " + exception.getMessage());
    }

    /**
     * 시간을 mm:ss 형식으로 포맷
     */
    private String formatTime(long timeInMillis) {
        if (timeInMillis == Long.MAX_VALUE) {
            return "LIVE";
        }

        long minutes = (timeInMillis / 1000) / 60;
        long seconds = (timeInMillis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
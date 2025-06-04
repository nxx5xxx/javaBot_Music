package org.nxx5xxx.javaBot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    /**
     * 트랙을 대기열에 추가하거나 즉시 재생
     */
    public void queue(AudioTrack track) {
        // 현재 재생 중인 트랙이 없으면 즉시 재생
        if (!player.startTrack(track, true)) {
            // 재생 중이면 대기열에 추가
            queue.offer(track);
        }
    }

    /**
     * 다음 트랙으로 넘어가기
     */
    public void nextTrack() {
        AudioTrack nextTrack = queue.poll();
        player.startTrack(nextTrack, false);
    }

    /**
     * 재생 정지 및 대기열 비우기
     */
    public void stop() {
        player.stopTrack();
        queue.clear();
    }

    /**
     * 현재 대기열 정보 반환
     */
    public String getQueueInfo() {
        if (queue.isEmpty()) {
            AudioTrack currentTrack = player.getPlayingTrack();
            if (currentTrack == null) {
                return "현재 재생 중인 음악이 없습니다.";
            } else {
                return "**현재 재생 중:** " + currentTrack.getInfo().title +
                        " (" + formatTime(currentTrack.getPosition()) +
                        "/" + formatTime(currentTrack.getDuration()) + ")";
            }
        }

        StringBuilder sb = new StringBuilder();
        AudioTrack currentTrack = player.getPlayingTrack();

        if (currentTrack != null) {
            sb.append("**현재 재생 중:** ").append(currentTrack.getInfo().title)
                    .append(" (").append(formatTime(currentTrack.getPosition()))
                    .append("/").append(formatTime(currentTrack.getDuration())).append(")\n\n");
        }

        sb.append("**대기열:**\n");
        int trackNumber = 1;
        for (AudioTrack track : queue) {
            sb.append(trackNumber++).append(". ")
                    .append(track.getInfo().title)
                    .append(" (").append(formatTime(track.getDuration())).append(")\n");

            // 너무 긴 목록 방지
            if (trackNumber > 10) {
                sb.append("... 그리고 ").append(queue.size() - 10).append("개 더");
                break;
            }
        }

        return sb.toString();
    }

    /**
     * 시간을 mm:ss 형식으로 포맷
     */
    private String formatTime(long timeInMillis) {
        long minutes = (timeInMillis / 1000) / 60;
        long seconds = (timeInMillis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // 트랙이 끝나면 자동으로 다음 트랙 재생
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        System.out.println("재생 시작: " + track.getInfo().title);
    }
}
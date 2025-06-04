package org.nxx5xxx.javaBot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer audioPlayer;
    private final BlockingQueue<AudioTrack> queue;

    public TrackScheduler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track) {
        System.out.println("큐에 트랙 추가 시도: " + track.getInfo().title);

        // 현재 재생 중인 트랙이 있는지 확인
        if (this.audioPlayer.getPlayingTrack() == null) {
            // 재생 중인 트랙이 없으면 바로 재생
            System.out.println("바로 재생 시작");
            boolean started = this.audioPlayer.startTrack(track, false);
            System.out.println("재생 시작 결과: " + started);
        } else {
            // 재생 중인 트랙이 있으면 큐에 추가
            System.out.println("큐에 추가됨");
            this.queue.offer(track);
        }
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        System.out.println("트랙 시작됨: " + track.getInfo().title);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        System.out.println("트랙 종료: " + track.getInfo().title + ", 이유: " + endReason);

        // LOAD_FAILED 원인 분석
        if (endReason == AudioTrackEndReason.LOAD_FAILED) {
            System.err.println("❌ 로드 실패 상세 정보:");
            System.err.println("- 트랙 URL: " + track.getInfo().uri);
            System.err.println("- 트랙 길이: " + track.getDuration());
            System.err.println("- 재생 위치: " + track.getPosition());
        }

        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        System.err.println("트랙 예외 발생: " + exception.getMessage());
        exception.printStackTrace();
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        System.err.println("트랙이 멈춤: " + track.getInfo().title + " (임계값: " + thresholdMs + "ms)");
    }

    public void nextTrack() {
        AudioTrack nextTrack = this.queue.poll();
        if (nextTrack != null) {
            System.out.println("다음 트랙 재생: " + nextTrack.getInfo().title);
            this.audioPlayer.startTrack(nextTrack, false);
        } else {
            System.out.println("큐가 비어있음");
        }
    }

    // 추가 유틸리티 메서드들
    public void clearQueue() {
        this.queue.clear();
    }

    public int getQueueSize() {
        return this.queue.size();
    }

    public boolean isEmpty() {
        return this.queue.isEmpty() && this.audioPlayer.getPlayingTrack() == null;
    }
}
package org.nxx5xxx.javaBot;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.nio.ByteBuffer;

public class MusicManager {
    private final AudioPlayerManager playerManager;
    private final AudioPlayer player;
    public final TrackScheduler scheduler;
    private final AudioPlayerSendHandler sendHandler;

    public MusicManager() {
        this.playerManager = new DefaultAudioPlayerManager();
        // 다양한 오디오 소스 활성화 (YouTube, SoundCloud 등)
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);

        this.player = playerManager.createPlayer();
        this.scheduler = new TrackScheduler(player);
        this.player.addListener(scheduler);
        this.sendHandler = new AudioPlayerSendHandler(player);
    }

    public AudioPlayerSendHandler getSendHandler() {
        return sendHandler;
    }

    public void loadAndPlay(TextChannel channel, String trackUrl) {
        playerManager.loadItemOrdered(this, trackUrl, new MusicLoadHandler(channel, scheduler));
    }

    // Discord에 오디오를 전송하는 핸들러
    private static class AudioPlayerSendHandler implements AudioSendHandler {
        private final AudioPlayer audioPlayer;
        private AudioFrame lastFrame;

        public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
            this.audioPlayer = audioPlayer;
        }

        @Override
        public boolean canProvide() {
            if (lastFrame == null) {
                lastFrame = audioPlayer.provide();
            }
            return lastFrame != null;
        }

        @Override
        public ByteBuffer provide20MsAudio() {
            if (lastFrame == null) {
                lastFrame = audioPlayer.provide();
            }

            byte[] data = lastFrame != null ? lastFrame.getData() : null;
            lastFrame = null;

            return data != null ? ByteBuffer.wrap(data) : null;
        }

        @Override
        public boolean isOpus() {
            return true;
        }
    }
}
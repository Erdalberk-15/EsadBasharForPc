package com.melikshotroulette.melikshotroulette.utils

import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.util.Duration

object SoundManager {
    private var backgroundMusicPlayer: MediaPlayer? = null
    
    fun playBackgroundMusic() {
        try {
            val musicUrl = javaClass.getResource("/com/melikshotroulette/melikshotroulette/sounds/bg_music.mp3")
            if (musicUrl != null) {
                val media = Media(musicUrl.toString())
                backgroundMusicPlayer = MediaPlayer(media)
                backgroundMusicPlayer?.apply {
                    volume = 0.15  // Very low volume (15%)
                    cycleCount = MediaPlayer.INDEFINITE  // Loop forever
                    play()
                }
            } else {
                println("Background music file not found: bg_music.mp3")
            }
        } catch (e: Exception) {
            println("Error playing background music: ${e.message}")
        }
    }
    
    fun stopBackgroundMusic() {
        backgroundMusicPlayer?.stop()
        backgroundMusicPlayer?.dispose()
        backgroundMusicPlayer = null
    }
    
    fun playGunshot() {
        try {
            val soundUrl = javaClass.getResource("/com/melikshotroulette/melikshotroulette/sounds/ohh.mp3")
            if (soundUrl != null) {
                val media = Media(soundUrl.toString())
                val mediaPlayer = MediaPlayer(media)
                mediaPlayer.volume = 0.7
                mediaPlayer.play()
                
                // Clean up after playing
                mediaPlayer.setOnEndOfMedia {
                    mediaPlayer.dispose()
                }
            } else {
                println("Sound file not found: ohh.mp3")
            }
        } catch (e: Exception) {
            println("Error playing sound: ${e.message}")
        }
    }
}

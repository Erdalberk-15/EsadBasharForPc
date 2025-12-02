package com.esadbashar.esadbashar.utils

import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import java.net.URL

object SoundManager {
    
    private var mediaPlayer: MediaPlayer? = null
    private var isMuted = false
    
    /**
     * Play a sound file from resources
     * @param soundFileName The name of the sound file (e.g., "click.mp3")
     */
    fun playSound(soundFileName: String) {
        if (isMuted) return
        
        try {
            val soundUrl: URL? = javaClass.getResource("/com/esadbashar/esadbashar/sounds/$soundFileName")
            
            if (soundUrl != null) {
                val media = Media(soundUrl.toString())
                mediaPlayer?.stop()
                mediaPlayer = MediaPlayer(media)
                mediaPlayer?.play()
            } else {
                println("Sound file not found: $soundFileName")
            }
        } catch (e: Exception) {
            println("Error playing sound: ${e.message}")
        }
    }
    
    /**
     * Stop currently playing sound
     */
    fun stopSound() {
        mediaPlayer?.stop()
    }
    
    /**
     * Mute/unmute all sounds
     */
    fun setMuted(muted: Boolean) {
        isMuted = muted
        if (muted) {
            mediaPlayer?.stop()
        }
    }
    
    /**
     * Check if sounds are muted
     */
    fun isMuted(): Boolean = isMuted
    
    /**
     * Set volume (0.0 to 1.0)
     */
    fun setVolume(volume: Double) {
        mediaPlayer?.volume = volume.coerceIn(0.0, 1.0)
    }
}

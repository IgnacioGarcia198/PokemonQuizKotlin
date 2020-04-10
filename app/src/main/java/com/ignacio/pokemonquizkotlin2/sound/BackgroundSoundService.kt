package com.ignacio.pokemonquizkotlin2.sound

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import androidx.annotation.Nullable
import android.os.Binder
import com.ignacio.pokemonquizkotlin2.R


class BackgroundSoundService : Service(), MediaPlayer.OnErrorListener {
    private var length = 0
    private var mediaPlayer: MediaPlayer? = null
    enum class PlayerState {PLAYING, PAUSED, STOPPED}
    var playerState: PlayerState = PlayerState.STOPPED
    private set

    // This is the object that receives interactions from clients.
    private val mBinder = LocalBinder()
    inner class LocalBinder : Binder() {
        val service: BackgroundSoundService = this@BackgroundSoundService
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()

        mediaPlayer = MediaPlayer.create(this, R.raw.pokemonbgm)
        mediaPlayer?.isLooping = true // Set looping
        mediaPlayer?.setVolume(100f, 100f)
        mediaPlayer?.setOnErrorListener(this@BackgroundSoundService)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return startId
    }

    override fun onStart(intent: Intent, startId: Int) {}
    override fun onDestroy() {
        stopMusic()
    }

    fun stopMusic() {
        mediaPlayer?.let {
            it.stop()
            it.release()
            playerState = PlayerState.STOPPED
        }
    }

    override fun onLowMemory() {}

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        stopMusic()
        return false
    }

    fun pauseMusic() {
        mediaPlayer?.apply {
            if (isPlaying) {
                pause()
                length = currentPosition
                playerState = PlayerState.PAUSED
            }
        }
    }

    fun resumeMusic() {
        mediaPlayer?.apply {
            seekTo(length)
            start()
            playerState = PlayerState.PLAYING
        }
    }

    fun startMusic() {
        mediaPlayer?.apply {

            start()
            playerState = PlayerState.PLAYING
        }
    }
}
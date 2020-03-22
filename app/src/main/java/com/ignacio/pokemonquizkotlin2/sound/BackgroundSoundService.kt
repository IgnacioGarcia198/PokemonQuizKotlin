package com.ignacio.pokemonquizkotlin2.sound

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.Nullable
import android.os.Binder
import com.ignacio.pokemonquizkotlin2.R
import timber.log.Timber


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
        Timber.e("==== start command")
        /*mediaPlayer?.let {
           it.start()
           playerState = PlayerState.PLAYING
        }*/
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



    /*internal lateinit var player: MediaPlayer
    private var length = 0
    override fun onBind(arg0: Intent): IBinder? {

        return mBinder
    }

    inner class ServiceBinder : Binder() {
        val service : BackgroundSoundService
        get() = this@BackgroundSoundService
    }

    override fun onCreate() {
        super.onCreate()
        val afd = applicationContext.assets.openFd("pokemonbgm.wav") as AssetFileDescriptor
        val player = MediaPlayer()
        player.setDataSource(afd.fileDescriptor)
        player.isLooping = true // Set looping
        player.setVolume(100f, 100f)

        player.setOnErrorListener(object : MediaPlayer.OnErrorListener {
            override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
                onError(mp,what,extra)
                return true
            }

        }

        )
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Timber.i("starting player")
        player.start()
        return Service.START_NOT_STICKY
    }

    override fun onStart(intent: Intent, startId: Int) {
        // TO DO
    }

    fun onUnBind(arg0: Intent): IBinder? {
        // TO DO Auto-generated method
        return null
    }

    fun pauseMusic() {
        //if (player != null) {
            if (player.isPlaying()) {
                player.pause()
                length = player.getCurrentPosition()
            }
        //}
    }

    fun resumeMusic() {
        //if (player != null) {
            if (!player.isPlaying()) {
                player.seekTo(length)
                player.start()
            }
        //}
    }

    fun startMusic() {

        player.setOnErrorListener(this)
        //if (player != null) {
            player.start()
        //}
    }

    fun stopMusic() {
        //if (player != null) {
            player.stop()
            player.release()
            //player = null
        //}
    }

    fun onStop() {

    }

    fun onPause() {

    }

    override fun onDestroy() {
        player.stop()
        player.release()
    }

    override fun onLowMemory() {

    }

    companion object {
        private val TAG: String? = null
    }



    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Toast.makeText(this, "Music player failed", Toast.LENGTH_SHORT).show();
        //if (player != null) {
            //try {
                player.stop()
                player.release()
            //} //finally {
                //player = null
            //}
        //}
        return false
    }*/
}
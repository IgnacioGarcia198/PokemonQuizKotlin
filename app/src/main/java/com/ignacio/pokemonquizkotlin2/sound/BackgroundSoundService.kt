package com.ignacio.pokemonquizkotlin2.sound

import android.app.Service
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.widget.Toast
import timber.log.Timber


class BackgroundSoundService : Service(), MediaPlayer.OnErrorListener {
    private val mBinder: IBinder = ServiceBinder()
    internal lateinit var player: MediaPlayer
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
    }
}
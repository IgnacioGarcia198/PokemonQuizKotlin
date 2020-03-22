package com.ignacio.pokemonquizkotlin2

import android.os.Bundle
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.IBinder
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ignacio.pokemonquizkotlin2.databinding.ActivityMainBinding
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import javax.inject.Inject
import com.ignacio.pokemonquizkotlin2.sound.BackgroundSoundService.LocalBinder
import timber.log.Timber
// TODO FIX ITS ALWAYS STARTING PLAYER ON CONFIG CHANGES.
class MainActivity : AppCompatActivity(), HasAndroidInjector, MediaPlayer.OnErrorListener {

    //private var backgroundSoundService : BackgroundSoundService? = null
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector
    //private var mIsBound: Boolean = false
    private var buttonIsPlaying: Boolean = false
    private var firsttime = true

    private var length = 0
    private var mediaPlayer: MediaPlayer? = null
    enum class PlayerState {PLAYING, PAUSED, STOPPED}
    var playerState: PlayerState = PlayerState.STOPPED
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        val toolbar: Toolbar = toolbar
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        //val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_daily_pokemon)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_daily_pokemon
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
        //BIND Music Service
        savedInstanceState?.let {
            buttonIsPlaying = it.getBoolean("buttonIsPlaying", false)
            firsttime = it.getBoolean("firsttime", true)
        }
        //doBindService()

    }

    private fun createMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, R.raw.pokemonbgm)
        mediaPlayer?.isLooping = true // Set looping
        mediaPlayer?.setVolume(100f, 100f)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main2, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        menu?.let {
            menu.findItem(R.id.action_play_music).icon =
            if(mediaPlayer != null && playerState == PlayerState.PLAYING) {
                ContextCompat.getDrawable(
                    this@MainActivity, R.drawable.ic_pause_circle_outline_black_48dp)
            }
            else {
                ContextCompat.getDrawable(
                    this@MainActivity, R.drawable.ic_play_circle_outline_black_48dp)
            }
            return true
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_settings -> {return true}
            R.id.action_play_music -> {
                if(mediaPlayer != null && playerState == PlayerState.PLAYING) {
                    pauseMusic()
                }
                else resumeMusic()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_daily_pokemon)
        return NavigationUI.navigateUp(navController, drawer_layout) || super.onSupportNavigateUp()
    }

    fun stopMusic() {
        mediaPlayer?.let {
            it.stop()
            it.release()
            playerState = PlayerState.STOPPED
        }
    }

    /*private val musicServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            backgroundSoundService = null
            invalidateOptionsMenu()
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            name?.let {
                if(name.className == BackgroundSoundService::class.java.name) {
                    backgroundSoundService = (service as BackgroundSoundService.LocalBinder).service
                    backgroundSoundService?.let {
                        if(firsttime && it.playerState != BackgroundSoundService.PlayerState.PLAYING) {
                            startMusic()
                            firsttime = false
                        }
                    }
                }
            }
        }
    }*/

    private fun startMusic() {
        mediaPlayer?.apply {
            setOnErrorListener(this@MainActivity)
            start()
            playerState = PlayerState.PLAYING
        }
        invalidateOptionsMenu()
        buttonIsPlaying = true
    }

    private fun resumeMusic() {
        if(buttonIsPlaying && playerState != PlayerState.PLAYING) {
            mediaPlayer?.apply {
                seekTo(length)
                start()
                playerState = PlayerState.PLAYING
            }
            invalidateOptionsMenu()
            buttonIsPlaying = true
        }

    }

    private fun pauseMusic() {
        if(!isChangingConfigurations) {
            mediaPlayer?.apply {
                if (isPlaying) {
                    pause()
                    length = currentPosition
                    playerState = PlayerState.PAUSED
                }
            }
            invalidateOptionsMenu()
            buttonIsPlaying = false
        }
    }

    override fun onStart() {
        super.onStart()
        createMediaPlayer()
        startMusic()
    }

    override fun onResume() {
        super.onResume()
        createMediaPlayer()
        startMusic()

        //resumeMusic()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startMusic()
    }

    override fun onStop() {
        super.onStop()
        pauseMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(!isChangingConfigurations) stopMusic()
    }

    /*fun doBindService() {
        bindService(Intent(this, BackgroundSoundService::class.java),
        musicServiceConnection,
        Context.BIND_AUTO_CREATE)
        mIsBound = true
    }

    fun doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(musicServiceConnection)
            mIsBound = false
        }
    }*/

    override fun onPause() {
        super.onPause()
        pauseMusic()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putBoolean("buttonIsPlaying", buttonIsPlaying)
        outState.putBoolean("firsttime", firsttime)
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        stopMusic()
        return false
    }
}

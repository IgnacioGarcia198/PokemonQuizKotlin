package com.ignacio.pokemonquizkotlin2

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
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
import com.ignacio.pokemonquizkotlin2.sound.BackgroundSoundService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var backgroundSoundService: BackgroundSoundService? = null
    private var mIsBound: Boolean = false
    private var buttonIsPlaying: Boolean = true
    private var firsttime = true

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
        doBindService()

    }

    /*private fun createMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, R.raw.pokemonbgm)
        mediaPlayer?.isLooping = true // Set looping
        mediaPlayer?.setVolume(100f, 100f)
    }*/

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main2, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        menu?.let {

            backgroundSoundService?.let {
                val bgmitem = menu.findItem(R.id.action_play_music)
                with(bgmitem) {
                    if (it.playerState == BackgroundSoundService.PlayerState.PLAYING) {
                        icon = ContextCompat.getDrawable(
                            this@MainActivity, R.drawable.ic_music_note_white_48dp
                        )
                        title = getString(R.string.pause_bgm)
                    } else {
                        icon = ContextCompat.getDrawable(
                            this@MainActivity, R.drawable.ic_music_note_gray_48dp
                        )
                        title = getString(R.string.play_bgm)
                    }
                }

            }

            return true
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                return true
            }
            R.id.action_play_music -> {
                backgroundSoundService?.let {
                    buttonIsPlaying =
                        if (it.playerState == BackgroundSoundService.PlayerState.PLAYING) {
                            pauseMusic()
                            false
                        } else {
                            resumeMusic()
                            true
                        }
                }
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
        backgroundSoundService?.stopMusic()
    }

    private val musicServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            backgroundSoundService = null
            invalidateOptionsMenu()
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            name?.let {
                if (name.className == BackgroundSoundService::class.java.name) {
                    backgroundSoundService = (service as BackgroundSoundService.LocalBinder).service
                    backgroundSoundService?.let {
                        if (firsttime && buttonIsPlaying && it.playerState != BackgroundSoundService.PlayerState.PLAYING) {
                            resumeMusic()
                            firsttime = false
                        }
                    }
                }
            }
        }
    }

    private fun startMusic() {
        backgroundSoundService?.startMusic()
    }

    private fun resumeMusic() {
        backgroundSoundService?.resumeMusic()
        invalidateOptionsMenu()
    }

    private fun pauseMusic() {
        if (!isChangingConfigurations) {
            backgroundSoundService?.pauseMusic()
            invalidateOptionsMenu()
        }
    }

    override fun onResume() {
        super.onResume()
        backgroundSoundService?.let {
            if (buttonIsPlaying && it.playerState != BackgroundSoundService.PlayerState.PLAYING) {
                resumeMusic()
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startMusic()
    }

    override fun onStop() {
        super.onStop()
        pauseMusic()
        if (!isChangingConfigurations) stopMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isChangingConfigurations)
            doUnbindService()
    }

    fun doBindService() {
        bindService(
            Intent(this, BackgroundSoundService::class.java),
            musicServiceConnection,
            Context.BIND_AUTO_CREATE
        )
        mIsBound = true
    }

    fun doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(musicServiceConnection)
            mIsBound = false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("buttonIsPlaying", buttonIsPlaying)
        outState.putBoolean("firsttime", firsttime)
    }
}

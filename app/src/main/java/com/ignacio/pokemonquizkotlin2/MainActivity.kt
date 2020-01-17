package com.ignacio.pokemonquizkotlin2

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ignacio.pokemonquizkotlin2.databinding.ActivityMainBinding
import com.ignacio.pokemonquizkotlin2.sound.BackgroundSoundService
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import javax.inject.Inject


class MainActivity : AppCompatActivity(), HasAndroidInjector {

    //private lateinit var appBarConfiguration: AppBarConfiguration
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this,R.layout.activity_main)
        val toolbar: Toolbar = toolbar
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        //val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_daily_pokemon)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_daily_pokemon),drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        //setupActionBarWithNavController(this, navController, drawerLayout)
        binding.navView.setupWithNavController(navController)


        //BIND Music Service
        doBindService()
        val music = Intent()
        music.setClass(this, BackgroundSoundService::class.java)
        startService(music)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main2, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_daily_pokemon)
        return NavigationUI.navigateUp(navController,drawer_layout) || super.onSupportNavigateUp()
    }


    //Bind/Unbind music service
    private var mIsBound = false
    private var mServ: BackgroundSoundService? = null
    private val Scon: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            mServ = (binder as BackgroundSoundService.ServiceBinder).service
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mServ = null
        }
    }

    fun doBindService() {
        bindService(
            Intent(this, BackgroundSoundService::class.java),
            Scon, Context.BIND_AUTO_CREATE
        )
        mIsBound = true
    }

    fun doUnbindService() {
        if (mIsBound) {
            unbindService(Scon)
            mIsBound = false
        }
    }

    override fun onResume() {
        super.onResume()
        if (mServ != null) {
            mServ!!.resumeMusic()
        }
    }

    override fun onPause() {
        super.onPause()
        //Detect idle screen
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        var isScreenOn = false
        //if (pm != null) {
            isScreenOn = pm.isScreenOn
        //}
        if (!isScreenOn) {
            if (mServ != null) {
                mServ!!.pauseMusic()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        //UNBIND music service
        doUnbindService()
        val music = Intent()
        music.setClass(this, BackgroundSoundService::class.java)
        stopService(music)
    }

}

package com.ignacio.pokemonquizkotlin2.utils

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import java.lang.ref.WeakReference

/**
 * A timer that counts only forward, it can be paused, stopped and reset.
 */
enum class TimerState {
    STARTED, STOPPED, PAUSED
}

abstract class CountBaseTimer constructor(val interval : Long) {
    companion object {
        const val MSG = 1
        //var handler : Handler
    }
    protected var timerState = TimerState.STOPPED
    var base :Long = 0L
    protected var pauseTime = 0L
    var elapsedTime = 0L

    abstract fun onTick(elapsedTime : Long)
    protected var looper : Looper? = null
    protected var handler : WeakReference<Handler>? = null

    fun stop() {
        handler?.get()?.removeMessages(MSG)
        timerState = TimerState.STOPPED
        if(looper != Looper.getMainLooper())
            looper?.quit()
    }

    fun reset() {
        synchronized (this) {
            base = SystemClock.elapsedRealtime()
        }
    }

    fun pause() {
        if(timerState == TimerState.STARTED) {
            pauseTime = SystemClock.elapsedRealtime()
            handler?.get()?.removeMessages(MSG)
            timerState = TimerState.PAUSED
            if(looper != Looper.getMainLooper())
                looper?.quit()
        }
    }

    fun start() { // do not reset before start...
        if(timerState == TimerState.STARTED) {
            return
        }
        if(timerState == TimerState.PAUSED) { // restarting from pause
            base += SystemClock.elapsedRealtime() - pauseTime
        }
        else { // fresh start after stop or initially
            base = SystemClock.elapsedRealtime() // starting for the first time
        }

        timerState = TimerState.STARTED
        getMyLooper()
        handler = WeakReference(createHandler(looper!!))
        handler?.get()?.sendMessage(handler!!.get()!!.obtainMessage(MSG))
    }

    protected fun getMyLooper() {
        looper = Looper.myLooper()
        if(looper == null) {
            Looper.prepare()
            looper = Looper.myLooper()
            if(looper == null)
                Looper.loop()
        }
    }

    // must be overriden
    protected abstract fun createHandler(looper: Looper) : Handler
        //return Handler(looper!!)

}

abstract class CountUpTimer(interval: Long) : CountBaseTimer(interval) {
    override fun createHandler(looper: Looper): Handler {
        return CountUpHandler(this, looper)
    }
}

class CountUpHandler(val countUpTimer: CountUpTimer, looper: Looper) : Handler(looper) {
    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        synchronized (countUpTimer) {
            with(countUpTimer) {
                val thistickStart = SystemClock.elapsedRealtime()
                elapsedTime = SystemClock.elapsedRealtime() - base
                onTick(elapsedTime)
                val thistickduration = SystemClock.elapsedRealtime()-thistickStart
                sendMessageDelayed(obtainMessage(CountBaseTimer.MSG), interval-thistickduration)
            }
        }
    }
}



abstract class CountUpDownTimer(interval: Long, val initialTime : Long) : CountBaseTimer(interval) {
    override fun createHandler(looper: Looper): Handler {
        return CountUpDownHandler(this, looper)
    }

    abstract fun onFinish()
    abstract fun onDownTick(remainingTime: Long)
}

class CountUpDownHandler(val countUpDownTimer: CountUpDownTimer, looper: Looper) : Handler(looper) {

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        synchronized (countUpDownTimer) {
            with(countUpDownTimer) {
                val thistickStart = SystemClock.elapsedRealtime()
                elapsedTime = SystemClock.elapsedRealtime() - base
                val remainingTime = initialTime - elapsedTime
                if(remainingTime <= 0L) {
                    stop()
                    onFinish()
                    return
                }
                onTick(interval)
                onDownTick(remainingTime)
                val thistickduration = SystemClock.elapsedRealtime()-thistickStart
                sendMessageDelayed(obtainMessage(CountBaseTimer.MSG), interval-thistickduration)
            }

        }
    }
}
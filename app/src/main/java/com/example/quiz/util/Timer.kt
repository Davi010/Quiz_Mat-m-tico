package com.example.quiz.util

import android.os.CountDownTimer
import android.widget.TextView

class CustomCountdownTimer(
    private val millisInFuture: Long,
    private val countDownInterval: Long,
    private val timeTxt: TextView
) {
    private var timer: CountDownTimer
    var isRunning = false

    init {
        timer = object : CountDownTimer(millisInFuture, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000).toInt()
                timerFormat(secondsLeft)
            }

            override fun onFinish() {
                onFinish?.invoke()
            }
        }
    }

    var onTick: ((millisUntilFinished: Long) -> Unit)? = null
    var onFinish: (() -> Unit)? = null

    fun startTimer() {
        timer.start()
        isRunning = true
    }

    fun pauseTimer() {
        timer.cancel()
        isRunning = false
    }

    private fun timerFormat(secondsLeft: Int) {
        val minutes = secondsLeft / 60
        val seconds = secondsLeft % 60
        val timeFormat = "%02d:%02d".format(minutes, seconds)
        timeTxt.text = timeFormat
    }


}

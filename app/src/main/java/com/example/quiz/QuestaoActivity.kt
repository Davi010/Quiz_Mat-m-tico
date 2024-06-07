package com.example.quiz

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.quiz.databinding.ActivityQuestaoBinding
import com.example.quiz.models.lista_de_questoesItem
import com.example.quiz.util.CustomCountdownTimer
import com.example.quiz.util.SharedPreferenceManger
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.nio.charset.Charset
import java.text.DecimalFormat

class QuestaoActivity : AppCompatActivity() {

    private val gameScoreNextRoundDialog: Dialog by lazy {
        Dialog(this, R.style.DialogCustomTheme).apply {
            setContentView(R.layout.pontos_do_jogo)
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackPressedMethod()
        }
    }

    private val countdownTime = 60 // 60 segundos
    private lateinit var questionTimer: CustomCountdownTimer
    private var levelSize = 100
    private var nivel = 0
    private var correctQuestionPos = 0
    private var correctAnswerStr = ""

    private lateinit var correctScoreTxt: TextView
    private lateinit var wrongScoreTxt: TextView
    private lateinit var nextResumeorRestartBtn: Button

    private val sharedPreferenceManger: SharedPreferenceManger by lazy {
        SharedPreferenceManger(this)
    }

    private lateinit var questionbinding: ActivityQuestaoBinding // Declaração correta do binding

    private lateinit var questionList: ArrayList<ArrayList<lista_de_questoesItem>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        questionbinding = ActivityQuestaoBinding.inflate(layoutInflater)
        setContentView(questionbinding.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        questionbinding.toollbarLayout.imgfundo.setOnClickListener {
            onBackPressedMethod()
        }

        nivel = intent.getIntExtra("nivel", 0)
        val jsonStr = loadJsonFromAssets("question.json")
        Log.d("jsonStr", jsonStr)

        val questionItemList: ArrayList<lista_de_questoesItem> = Gson().fromJson(
            jsonStr, object : TypeToken<ArrayList<lista_de_questoesItem>>() {}.type
        )
        Log.d("ListadeQuestões", questionItemList.toString())

        questionList = questionItemList.chunked(10) as ArrayList<ArrayList<lista_de_questoesItem>>

        setBtnOnClick()

        questionTimer = CustomCountdownTimer(
            countdownTime * 1000L,
            1000L,
            questionbinding.timeTxt // Uso correto do binding
        ).apply {
            onFinish = {
                gameScoreNextRoundShow()
            }
        }

        questionbinding.toollbarLayout.titulo.text = "Nível: ${nivel + 1}"
        gameScoreNextRound()
        questionbinding.circularProgressBar.max = countdownTime
        questionbinding.circularProgressBar.progress = countdownTime
        question()
        questionTimer.startTimer()
    }

    private fun timerFormat(secondsLeft: Int) {
        questionbinding.circularProgressBar.progress = secondsLeft
        val decimalFormat = DecimalFormat("00")
        val timeFormat1 = decimalFormat.format(secondsLeft)
        questionbinding.timeTxt.text = timeFormat1
    }

    private fun setBtnOnClick() {
        questionbinding.primeiraopc.setOnClickListener { setAnswerTxt(it) }
        questionbinding.segundaopc.setOnClickListener { setAnswerTxt(it) }
        questionbinding.terceiraopc.setOnClickListener { setAnswerTxt(it) }
        questionbinding.quartaopc.setOnClickListener { setAnswerTxt(it) }
    }

    private var mLastClickTime: Long = 0
    private fun setAnswerTxt(view: View) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()

        val answerTxt = (view as TextView).text.toString()
        Log.d("respostaTxt", answerTxt)
        Log.d("RespostaCorretaStr", correctAnswerStr)
        if (correctAnswerStr.equals(answerTxt, true)) {
            view.setBackgroundColor(Color.GREEN)
            correctQuestionPos += 1
            questionbinding.noOfQuestiontxt.text = "Questão ${correctQuestionPos + 1} / 10"
            if (correctQuestionPos > 9) {
                correctScoreTxt.text = correctQuestionPos.toString()
                wrongScoreTxt.text = (10 - correctQuestionPos).toString()
                sharedPreferenceManger.setLevelState("Nivel$nivel", true)
                nextResumeorRestartBtn.text = "Próximo"
                questionTimer.pauseTimer()
                gameScoreNextRoundDialog.show()
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    nextQuestion()
                }, 1000)
            }
        } else {
            view.setBackgroundColor(Color.RED)
            gameScoreNextRoundShow()
        }
    }

    private fun nextQuestion() {
        question()
        questionbinding.noOfQuestiontxt.text = "Questão ${correctQuestionPos + 1} / 10"
    }

    private fun gameScoreNextRound() {
        correctScoreTxt = gameScoreNextRoundDialog.findViewById(R.id.pontosC)
        wrongScoreTxt = gameScoreNextRoundDialog.findViewById(R.id.pontosE)
        nextResumeorRestartBtn = gameScoreNextRoundDialog.findViewById(R.id.btnRs)

        nextResumeorRestartBtn.setOnClickListener {
            if (nextResumeorRestartBtn.text.toString().equals("Resumo", true)) {
                gameScoreNextRoundDialog.dismiss()
                onResume()
            } else {
                correctQuestionPos = 0
                if (nextResumeorRestartBtn.text.toString().equals("Próximo", true)) {
                    nivel += 1
                }
                gameScoreNextRoundDialog.dismiss()
                questionbinding.noOfQuestiontxt.text = "Questão ${correctQuestionPos + 1} / 10"
                if (nivel < levelSize) {
                    questionbinding.toollbarLayout.titulo.text = "Nivel:${nivel + 1}"
                    nextQuestion()
                    questionbinding.circularProgressBar.max = countdownTime
                    questionbinding.circularProgressBar.progress = countdownTime
                    questionTimer.startTimer()
                } else {
                    MaterialAlertDialogBuilder(this).setTitle("Todos os niveis completos")
                        .setPositiveButton("Ok") { _, _ ->
                            finish()
                        }
                        .setCancelable(false)
                        .show()
                }
            }
        }
        val nivelBtn = gameScoreNextRoundDialog.findViewById<Button>(R.id.btnNv)
        nivelBtn.setOnClickListener {
            gameScoreNextRoundDialog.dismiss()
            finish()
        }
    }

    private fun question() {
        val question = questionList[nivel][correctQuestionPos]

        correctAnswerStr = question.questionSplit.questionMarkValue
        questionbinding.questionTxt.text = question.questionTxt

        val optionsList = question.options.shuffled()

        questionbinding.primeiraopc.setBackgroundColor(Color.WHITE)
        questionbinding.segundaopc.setBackgroundColor(Color.WHITE)
        questionbinding.terceiraopc.setBackgroundColor(Color.WHITE)
        questionbinding.quartaopc.setBackgroundColor(Color.WHITE)

        questionbinding.primeiraopc.text = optionsList[0]
        questionbinding.segundaopc.text = optionsList[1]
        questionbinding.terceiraopc.text = optionsList[2]
        questionbinding.quartaopc.text = optionsList[3]
    }

    private fun onBackPressedMethod() {
        questionTimer.pauseTimer()
        correctScoreTxt.text = correctQuestionPos.toString()
        wrongScoreTxt.text = (10 - correctQuestionPos).toString()
        nextResumeorRestartBtn.text = "Reiniciar"
        gameScoreNextRoundDialog.show()
    }

    private fun gameScoreNextRoundShow() {
        questionTimer.pauseTimer()
        correctScoreTxt.text = correctQuestionPos.toString()
        wrongScoreTxt.text = (10 - correctQuestionPos).toString()
        nextResumeorRestartBtn.text = if (correctQuestionPos == 9) "Próximo" else "Reiniciar"
        gameScoreNextRoundDialog.show()
    }

    private fun loadJsonFromAssets(filename: String): String {
        var json: String? = null
        try {
            val inputStream = assets.open(filename)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charset.defaultCharset())
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return json ?: ""
    }

    override fun onPause() {
        questionTimer.pauseTimer()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (!gameScoreNextRoundDialog.isShowing) {
            questionTimer.pauseTimer()
        }
    }

    override fun onDestroy() {
        questionTimer.pauseTimer()
        super.onDestroy()
    }
}

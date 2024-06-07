package com.example.quiz

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import com.example.quiz.util.setupDialog
import kotlin.system.exitProcess
import android.util.Log  // Importar para usar Log.d

class BemvindoActivity : AppCompatActivity() {
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackPressedMethod()
        }
    }
    private val exitDialog: Dialog by lazy {
        Dialog(this, R.style.DialogCustomTheme).apply {
            setupDialog(R.layout.sair_do_jogo)
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bemvindo)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        splashScreen.setKeepOnScreenCondition { false }

        val btncomecou = findViewById<Button>(R.id.btncomecou)
        btncomecou.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.zoom_in_cut)
        )
        btncomecou.setOnClickListener {
            //tickMusic.start()
            Log.d("BemvindoActivity", "Iniciando NivelActivity")
            startActivity(Intent(this, NivelActivity::class.java))
        }

        val btnS: Button = exitDialog.findViewById(R.id.btnS)
        val btnN: Button = exitDialog.findViewById(R.id.btnN)

        btnS.setOnClickListener {
           // tickMusic.start()
            moveTaskToBack(true)
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(1)
        }
        btnN.setOnClickListener {
            //tickMusic.start()
            exitDialog.dismiss()
        }
    }

    private fun onBackPressedMethod() {
        exitDialog.show()
    }
}

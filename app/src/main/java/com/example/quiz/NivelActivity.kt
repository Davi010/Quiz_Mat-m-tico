package com.example.quiz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quiz.adapters.NivelAdapter
import com.example.quiz.databinding.ActivityNivelBinding
import com.example.quiz.util.SharedPreferenceManger

class NivelActivity : AppCompatActivity() {
    private val nivelBinding: ActivityNivelBinding by lazy {
        ActivityNivelBinding.inflate(layoutInflater)
    }

    private val sharedPreferenceManger: SharedPreferenceManger by lazy {
        SharedPreferenceManger(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(nivelBinding.root)

        nivelBinding.toollbarLayout.titulo.text = "Níveis"
        nivelBinding.toollbarLayout.imgfundo.setOnClickListener { finish() }

        val niveis = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        nivelBinding.niveis.adapter = NivelAdapter(this, sharedPreferenceManger)
    }

    override fun onResume() {
        super.onResume()
        nivelBinding.niveis.adapter?.notifyDataSetChanged()
    }
}

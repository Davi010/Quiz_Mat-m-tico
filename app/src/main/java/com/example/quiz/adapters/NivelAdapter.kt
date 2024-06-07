package com.example.quiz.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.QuestaoActivity
import com.example.quiz.R
import com.example.quiz.util.SharedPreferenceManger

class NivelAdapter(
    private val context: Context,
    private val sharedPreferenceManger: SharedPreferenceManger
) : RecyclerView.Adapter<NivelAdapter.NivelViewHolder>() {

    private val niveis = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NivelViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.nivel_layout, parent, false)
        return NivelViewHolder(view)
    }

    override fun onBindViewHolder(holder: NivelViewHolder, position: Int) {
        holder.bind(niveis[position])
        holder.itemView.setOnClickListener {
            val nivel = niveis[position]
            val intent = Intent(context, QuestaoActivity::class.java).apply {
                putExtra("nivel", nivel)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return niveis.size
    }

    inner class NivelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewNivel: TextView = itemView.findViewById(R.id.txtLevelName)

        fun bind(nivel: Int) {
            textViewNivel.text = "Nível $nivel"
        }
    }
}

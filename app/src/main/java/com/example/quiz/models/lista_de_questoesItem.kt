package com.example.quiz.models

data class lista_de_questoesItem(
    val options: List<String>,
    val questionSplit: QuestionSplit,
    val questionTxt: String,
    val type: String
)
package com.robbyyehezkiel.robustaroasting.data.model

data class Question(
    var description: String = "",
    var option1: String = "",
    var option2: String = "",
    var option3: String = "",
    var answer: String = "",
    var userAnswer: String = ""
)
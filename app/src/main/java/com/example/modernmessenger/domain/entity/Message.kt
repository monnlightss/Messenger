package com.example.modernmessenger.domain.entity

/** data класс сообщения в чате **/
data class Message(
    val senderId: String,
    val text: String,
    val date: String
)

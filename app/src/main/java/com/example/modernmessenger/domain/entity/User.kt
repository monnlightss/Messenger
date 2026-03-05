package com.example.modernmessenger.domain.entity

/** data класс пользователя **/
data class User(
    val id: String,
    val username: String,
    val profileImageUrl: String,
    val phoneNumber: String
)
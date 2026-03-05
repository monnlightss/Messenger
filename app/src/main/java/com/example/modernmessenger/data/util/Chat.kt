package com.example.modernmessenger.data.util
object Chat {
    //id чата между двумя пользователями генерируется следующим образом: userId1+userId2 -> сортируем получившуюся строку
    fun generateChatId(user1Id: String, user2Id: String): String = Str.sort(user1Id + user2Id)
}
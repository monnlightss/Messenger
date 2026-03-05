package com.example.modernmessenger.domain.repository

import com.example.modernmessenger.domain.entity.Chat
import com.example.modernmessenger.domain.entity.User

/** Интерфейс операций с пользователем (загрузка, загрузка чатов пользователя, загрузка всех пользователей, добавление чата пользователю) **/
interface UserRepository {
    suspend fun loadUser(id: String) : User?

    suspend fun loadUserChats(id: String) : List<Chat>

    suspend fun loadAllUsers() : List<User>

    suspend fun addChatToUser(userId: String, chatId: String, onSuccess: () -> Unit = {})
}
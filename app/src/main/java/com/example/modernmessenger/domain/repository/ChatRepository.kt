package com.example.modernmessenger.domain.repository

import com.example.modernmessenger.domain.entity.Chat
import com.example.modernmessenger.domain.entity.Message
import com.example.modernmessenger.domain.entity.Result
import com.example.modernmessenger.domain.entity.User

/** Интерфейс для работы с чатом **/
interface ChatRepository {
    suspend fun loadChat(id: String) : Chat?

    suspend fun createChat(chatId: String, chatInfo: HashMap<String, String>, onSuccess: () -> Unit)

    fun sendMessage(chatId: String, messageInfo: HashMap<String, String>)

    /** Функция чтобы загрузить пользователя с которым мы переписываемся **/
    suspend fun loadChatUser(currUserId: String, chatId: String) : User?
}
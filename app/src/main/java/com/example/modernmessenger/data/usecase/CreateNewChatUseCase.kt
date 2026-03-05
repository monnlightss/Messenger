package com.example.modernmessenger.data.usecase

import com.example.modernmessenger.data.util.Chat
import com.example.modernmessenger.domain.entity.User
import com.example.modernmessenger.domain.namespaces.AUTH
import com.example.modernmessenger.domain.namespaces.CHILD_ID
import com.example.modernmessenger.domain.namespaces.CHILD_USER1
import com.example.modernmessenger.domain.namespaces.CHILD_USER2
import com.example.modernmessenger.domain.repository.ChatRepository
import com.example.modernmessenger.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Этот класс отображает use case для добавления нового чата в БД. Для этого сначала нужно добавить  **/
class CreateNewChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) {
    fun execute(chatUser: User, onSuccess: (String) -> Unit) = CoroutineScope(Dispatchers.IO).launch {
        val uid = AUTH.currentUser?.uid ?: return@launch //получаем id пользователя

        //генерируем id чата
        val chatId = Chat.generateChatId(user1Id = uid, user2Id = chatUser.id)

        val chatInfo = hashMapOf(
            CHILD_USER1 to uid,
            CHILD_USER2 to chatUser.id
        )

        //вызываем фукнцию чтобы создать чат
        chatRepository.createChat(
            chatId = chatId,
            chatInfo = chatInfo
        ){
            //добавляем id чата к обоим пользователям
            CoroutineScope(Dispatchers.IO).launch {
                userRepository.addChatToUser(
                    userId = uid,
                    chatId = chatId
                ){
                    onSuccess(chatId)
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                userRepository.addChatToUser(
                    userId = chatUser.id,
                    chatId = chatId
                )
            }
        }
    }
}
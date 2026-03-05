package com.example.modernmessenger.data.repositoryImpl

import com.example.modernmessenger.domain.database.getDataOnce
import com.example.modernmessenger.domain.entity.Chat
import com.example.modernmessenger.domain.entity.User
import com.example.modernmessenger.domain.namespaces.AUTH
import com.example.modernmessenger.domain.namespaces.CHILD_MESSAGES
import com.example.modernmessenger.domain.namespaces.CHILD_USER1
import com.example.modernmessenger.domain.namespaces.CHILD_USER2
import com.example.modernmessenger.domain.namespaces.DB
import com.example.modernmessenger.domain.namespaces.NODE_CHATS
import com.example.modernmessenger.domain.repository.ChatRepository
import com.example.modernmessenger.domain.repository.UserRepository
import dagger.Lazy
import javax.inject.Inject

/** Implementation для интерфейса репозитория, отвечающего за чаты (загрузка чата, создание чата, загрузка сообщений
 *  из чата, отправка сообщения, загрузка пользователя) **/
class ChatRepositoryImpl @Inject constructor(
    private val userRepository: Lazy<UserRepository>
) : ChatRepository {
    /** Загрузка чата из Firebase Realtime Database (нашей БД) **/
    override suspend fun loadChat(id: String): Chat? {
        //получаем референс на данные
        val chatSnapshot = DB.child(NODE_CHATS).child(id).getDataOnce()

        //проверяем существует ли референс
        if (!chatSnapshot.exists()) return null

        //получаем id двух пользователей, которые переписываются
        val user1Id = (chatSnapshot.child(CHILD_USER1).value?:return null).toString()
        val user2Id = (chatSnapshot.child(CHILD_USER2).value?:return null).toString()

        //проверяем залогинен ли пользователь в свой аккаунт
        if (AUTH.currentUser?.uid==null) return null

        //id пользователя с которым мы переписываемся
        val chatUserId = if (user1Id==AUTH.currentUser?.uid) user2Id else user1Id
        val chatUser = userRepository.get().loadUser(id = chatUserId) ?: return null

        return Chat(
            id = id,
            chatUser = chatUser
        )
    }

    /** Функция создания чата **/
    override suspend fun createChat(chatId: String, chatInfo: HashMap<String, String>, onSuccess: () -> Unit){
        //Отправляем данные в БД, если все успешно вызываем callback
        DB.child(NODE_CHATS).child(chatId).updateChildren(chatInfo as Map<String, Any>)
            .addOnSuccessListener { onSuccess() }
    }

    /** Отправка сообщения в чат **/
    override fun sendMessage(chatId: String, messageInfo: HashMap<String, String>) {
        DB.child(NODE_CHATS).child(chatId).child(CHILD_MESSAGES).push().setValue(messageInfo)
    }

    /** Загрузка пользователя чата **/
    override suspend fun loadChatUser(currUserId: String, chatId: String): User? {
        //получаем референс
        val chatSnapshot = DB.child(NODE_CHATS).child(chatId).getDataOnce()

        //проверяем существование референса
        if (!chatSnapshot.exists()) return null

        val user1Id = (chatSnapshot.child(CHILD_USER1).value ?: return null).toString()
        val user2Id = (chatSnapshot.child(CHILD_USER2).value ?: return null).toString()

        //проверяем под каким пользователем мы сейчас залогинены
        val chatUserId = if (user1Id == currUserId) user2Id else user1Id

        return userRepository.get().loadUser(chatUserId)
    }
}
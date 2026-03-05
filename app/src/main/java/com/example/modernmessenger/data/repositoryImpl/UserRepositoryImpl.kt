package com.example.modernmessenger.data.repositoryImpl

import com.example.modernmessenger.domain.database.getDataOnce
import com.example.modernmessenger.domain.entity.Chat
import com.example.modernmessenger.domain.entity.User
import com.example.modernmessenger.domain.namespaces.AUTH
import com.example.modernmessenger.domain.namespaces.CHILD_CHATS
import com.example.modernmessenger.domain.namespaces.CHILD_PHONE_NUMBER
import com.example.modernmessenger.domain.namespaces.CHILD_PROFILE_IMAGE
import com.example.modernmessenger.domain.namespaces.CHILD_USERNAME
import com.example.modernmessenger.domain.namespaces.DB
import com.example.modernmessenger.domain.namespaces.NODE_USERS
import com.example.modernmessenger.domain.repository.ChatRepository
import com.example.modernmessenger.domain.repository.UserRepository
import dagger.Lazy
import javax.inject.Inject

/** Реализация интерфейса, отвечающего за пользователя в приложении **/
class UserRepositoryImpl @Inject constructor(
    private val chatRepository: Lazy<ChatRepository>
) : UserRepository {

    /** Загрузка пользователя **/
    override suspend fun loadUser(id: String): User? {
        //получаем референс на пользователя
        val userSnapshot = DB.child(NODE_USERS).child(id).getDataOnce()

        //проверяем что референс существует
        if (!userSnapshot.exists()) return null

        //получаем данные, создаем пользователя и возвращаем его
        val username = (userSnapshot.child(CHILD_USERNAME).value?:return null).toString()
        val profileImageUrl = (userSnapshot.child(CHILD_PROFILE_IMAGE).value?:"").toString()
        val phoneNumber = (userSnapshot.child(CHILD_PHONE_NUMBER).value?:"").toString()

        return User(
            id = id,
            username = username,
            profileImageUrl = profileImageUrl,
            phoneNumber = phoneNumber
        )
    }

    /** Загрузка чатов пользователя по его id **/
    override suspend fun loadUserChats(id: String): List<Chat> {
        //получаем референс в БД
        val userChats = DB.child(NODE_USERS).child(id).child(CHILD_CHATS).getDataOnce()

        //проверяем существование референса
        if (!userChats.exists()) return emptyList()

        //получаем id всех чатов пользователя и делаем сплит через запятую -> получаем массив id-шников чатов
        val chatsIds = (userChats.value?:return emptyList()).toString().split(",")
        val chats = ArrayList<Chat>()

        //проходимся по массиву id-шников
        for (chatId in chatsIds){
            val chat = chatRepository.get().loadChat(id = chatId)
            chat?.let{chats.add(chat)}
        }

        return chats
    }

    /** Загрузка всех пользователей из БД **/
    override suspend fun loadAllUsers(): List<User> {
        //получаем референс на всех пользователей в БД
        val usersSnapshot = DB.child(NODE_USERS).getDataOnce()

        val users = ArrayList<User>()
        for (userSnapshot in usersSnapshot.children){
            // нам не нужно загружать залогиненного пользователя в список
            if (userSnapshot.key == (AUTH.currentUser?.uid?:""))
                continue

            //загружаем пользователя по id
            val user = loadUser(id = userSnapshot.key.toString())
            user?.let{users.add(user)}
        }
        return users
    }

    /** Функция для добавления чата в чаты пользователя **/
    override suspend fun addChatToUser(userId: String, chatId: String, onSuccess: () -> Unit) {
        //получаем чаты пользователя
        var userChats = (DB.child(NODE_USERS).child(userId).child(CHILD_CHATS).getDataOnce().value?:"").toString()

        //если пользователь уже создал чат с этим человеком то нам не нужно добавлять ему чат еще раз
        if (userChats.split(",").contains(chatId)){
            onSuccess()
            return
        }

        //добавляем id в строку
        userChats += if (userChats.isEmpty()) chatId else ",${chatId}"

        //обновляем данные в БД
        DB.child(NODE_USERS).child(userId).child(CHILD_CHATS).setValue(userChats)
            .addOnSuccessListener { onSuccess() }
    }
}
package com.example.modernmessenger.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.modernmessenger.domain.entity.Message
import com.example.modernmessenger.domain.entity.User
import com.example.modernmessenger.domain.namespaces.AUTH
import com.example.modernmessenger.domain.namespaces.CHILD_DATE
import com.example.modernmessenger.domain.namespaces.CHILD_MESSAGES
import com.example.modernmessenger.domain.namespaces.CHILD_SENDER_ID
import com.example.modernmessenger.domain.namespaces.CHILD_TEXT
import com.example.modernmessenger.domain.namespaces.DB
import com.example.modernmessenger.domain.namespaces.NODE_CHATS
import com.example.modernmessenger.domain.repository.ChatRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel //указываем Dagger Hilt, что будем использовать его в этом классе (а также указываем что это viewmodel)
class ChatActivityViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    val chatUser = MutableLiveData<User>()
    val messages = MutableLiveData<List<Message>>()

    private val messagesValueEventListener = object: ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            //загружаем все сообщения чата
            val msgs = ArrayList<Message>()
            if (!snapshot.exists()){
                messages.postValue(emptyList())
                return
            }
            for (chatSnapshot in snapshot.children){
                //загружаем инфу о сообщении и добавляем в список
                val text = (chatSnapshot.child(CHILD_TEXT).value?:continue).toString()
                val date = (chatSnapshot.child(CHILD_DATE).value?:continue).toString()
                val senderId = (chatSnapshot.child(CHILD_SENDER_ID).value?:continue).toString()

                msgs.add(Message(senderId = senderId, text = text, date = date))
            }

            messages.postValue(msgs)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("Error", error.message)
        }
    }

    fun loadMessages(chatId: String) = viewModelScope.launch(Dispatchers.IO){
        //читаем сообщения из БД
        //добавляем слушатель на сообщения, чтобы обновлять их в real time
        DB.child(NODE_CHATS).child(chatId).child(CHILD_MESSAGES)
            .addValueEventListener(messagesValueEventListener)
    }

    //загружаем пользователя с которым переписываемся
    fun loadChatUser(chatId: String) = viewModelScope.launch(Dispatchers.IO){
        AUTH.currentUser?.uid?.let{ id->
            val user = chatRepository.loadChatUser(chatId = chatId, currUserId = id)
            user?.let{
                chatUser.postValue(it)
            }
        }
    }

    fun sendMessage(chatId: String, message: HashMap<String, String>) = viewModelScope.launch(Dispatchers.IO){
        chatRepository.sendMessage(chatId, message)
    }

    //останавливаем автоматическую загрузку сообщений, когда страница закрывается
    fun stopListeningToMessages(chatId: String){
        DB.child(NODE_CHATS).child(chatId).child(CHILD_MESSAGES).removeEventListener(messagesValueEventListener)
    }
}
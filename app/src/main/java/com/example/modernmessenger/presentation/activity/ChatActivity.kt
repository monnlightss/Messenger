package com.example.modernmessenger.presentation.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.modernmessenger.R
import com.example.modernmessenger.databinding.ActivityChatBinding
import com.example.modernmessenger.domain.entity.Message
import com.example.modernmessenger.domain.namespaces.AUTH
import com.example.modernmessenger.domain.namespaces.CHILD_DATE
import com.example.modernmessenger.domain.namespaces.CHILD_ID
import com.example.modernmessenger.domain.namespaces.CHILD_SENDER_ID
import com.example.modernmessenger.domain.namespaces.CHILD_TEXT
import com.example.modernmessenger.presentation.recyclerview.messages.MessagesAdapter
import com.example.modernmessenger.presentation.viewmodel.ChatActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {


    //viewBinding для упрощения работы с объектами из XML
    private lateinit var binding: ActivityChatBinding

    //view model
    private val viewModel: ChatActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //кнопка назад
        binding.backBtn.setOnClickListener {
            startActivity(Intent(this@ChatActivity, MainActivity::class.java))
            finish()
        }

        //получаем id чата из интента
        val chatId = intent.getStringExtra(CHILD_ID)

        //если chatId получен, то загружаем сообщения и пользователя с которым переписываемся
        chatId?.let{
            loadMessages(it)
            loadChatUser(it)
        }

        //отправка сообщения
        binding.sendBtn.setOnClickListener {
            //получаем сообщение из поля
            val message = binding.messageEt.text.toString()
            if (message.isEmpty()) return@setOnClickListener

            chatId?.let{id->
                sendMessage(id, message)
            }
        }
    }

    private fun loadMessages(chatId: String){
        //загружаем все сообщения в чате
        viewModel.messages.observe(this){messages->
            binding.messagesRv.layoutManager = LinearLayoutManager(this@ChatActivity)

            val adapter = MessagesAdapter(binding.messagesRv)
            adapter.setData(messages)
            binding.messagesRv.adapter = adapter
        }
        viewModel.loadMessages(chatId = chatId)
    }

    private fun loadChatUser(chatId: String){
        //загружаем аватарку и никнейм пользователя
        viewModel.chatUser.observe(this){user->
            binding.chatNameTv.text = user.username
            if (user.profileImageUrl.isNotEmpty())
                Glide.with(baseContext).load(user.profileImageUrl).into(binding.profileIv)
        }
        viewModel.loadChatUser(chatId)
    }

    private fun sendMessage(chatId: String, message: String){
        //получаем дату и время на данный момент
        val date = SimpleDateFormat("dd.MM.yyyy HH:mm").format(Date())

        //информация о сообщении
        val messageInfo = hashMapOf(
            CHILD_SENDER_ID to (AUTH.currentUser?.uid?:return),
            CHILD_TEXT to message,
            CHILD_DATE to date
        )
        //очищаем поле ввода
        binding.messageEt.setText("")

        //добавляем сообщение в список
        (binding.messagesRv.adapter as MessagesAdapter)
            .addMessage(Message(
                senderId = AUTH.currentUser?.uid?:return,
                text = message,
                date = date
            ))

        //отправляем сообщение в БД
        viewModel.sendMessage(chatId, messageInfo)
    }

    override fun onStop() {
        //нам нужно остановить автоматически читать сообщения чата из БД после того как мы вышли из активности
        intent.getStringExtra(CHILD_ID)?.let{
            viewModel.stopListeningToMessages(chatId = it)
        }

        super.onStop()
    }
}
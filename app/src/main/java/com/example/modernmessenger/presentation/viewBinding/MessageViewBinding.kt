package com.example.modernmessenger.presentation.viewBinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.example.modernmessenger.databinding.MessageFromCurrUserRvItemBinding
import com.example.modernmessenger.databinding.MessageRvItemBinding

/** Класс, который хранит viewbinding для наших сообщений
 * Имеет два вложенных класса: для viewbinding от данного пользователя или от другого пользователя (потому что разный xml layout)
 * **/
sealed class MessageViewBinding : ViewBinding {
    abstract val binding: ViewBinding

    class FromCurrUser(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        attachToParent: Boolean
    ) : MessageViewBinding() {
        override val binding: MessageFromCurrUserRvItemBinding =
            MessageFromCurrUserRvItemBinding.inflate(layoutInflater, parent, attachToParent)

        override fun getRoot(): View = binding.root
    }

    class FromChatUser(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        attachToParent: Boolean
    ) : MessageViewBinding() {
        override val binding: MessageRvItemBinding =
            MessageRvItemBinding.inflate(layoutInflater, parent, attachToParent)

        override fun getRoot(): View = binding.root
    }
}
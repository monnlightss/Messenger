package com.example.modernmessenger.presentation.recyclerview.messages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.modernmessenger.R
import com.example.modernmessenger.domain.entity.Message
import com.example.modernmessenger.domain.namespaces.AUTH
import com.example.modernmessenger.presentation.viewBinding.MessageViewBinding

class MessagesAdapter (
    private val recyclerView: RecyclerView
) : RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>(){

    /* специальная callback для ускоренного обновления recyclerview
    Принцип работы заключается в том, что обновляются только те элементы списка, данные которых либо они сами обновились
     */
    private val diffUtil: DiffUtil.ItemCallback<Message> = object: DiffUtil.ItemCallback<Message>(){
        //если ссылка на старый элемент совпадает с новым
        override fun areItemsTheSame(
            oldItem: Message,
            newItem: Message
        ): Boolean = oldItem===newItem

        //если содержание старого элемента совпадает с новым
        override fun areContentsTheSame(
            oldItem: Message,
            newItem: Message
        ): Boolean = oldItem == newItem
    }

    //утилита для быстрого асинхронного обновления списка
    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)

    fun setData(data: List<Message>){
        //добавляем данные в список
        asyncListDiffer.submitList(data)
    }

    fun addMessage(message: Message){
        val list = ArrayList(asyncListDiffer.currentList)
        list.add(message)
        //добавляем сообщение в список и скроллим на эту позицию
        asyncListDiffer.submitList(list){
            recyclerView.scrollToPosition(list.size-1)
        }
    }

    override fun getItemViewType(position: Int): Int {
        //если данный пользователь отправил сообщение - то отображаем его справа, если другой - то слева
        return if (asyncListDiffer.currentList[position].senderId == AUTH.currentUser?.uid)
            R.layout.message_from_curr_user_rv_item
        else R.layout.message_rv_item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        //определяем какой layout передать во ViewHolder
        return MessageViewHolder(
            if (viewType==R.layout.message_from_curr_user_rv_item)
                MessageViewBinding.FromCurrUser(LayoutInflater.from(parent.context), parent, false)
            else
                MessageViewBinding.FromChatUser(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = asyncListDiffer.currentList.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = asyncListDiffer.currentList[position]

        //в зависимости от того под какой layout наше сообщение, отображаем данные сообщения в UI
        when(holder.binding){
            is MessageViewBinding.FromCurrUser -> {
                val binding = (holder.binding as MessageViewBinding.FromCurrUser).binding
                binding.messageTv.text = message.text
                binding.messageDateTv.text = message.date
            }
            is MessageViewBinding.FromChatUser -> {
                val binding = (holder.binding as MessageViewBinding.FromChatUser).binding
                binding.messageTv.text = message.text
                binding.messageDateTv.text = message.date
            }
        }
    }

    class MessageViewHolder (var binding: MessageViewBinding) : RecyclerView.ViewHolder(binding.root)
}
package com.example.modernmessenger.presentation.recyclerview.chats

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.modernmessenger.R
import com.example.modernmessenger.domain.entity.Chat
import com.example.modernmessenger.domain.namespaces.CHILD_ID
import com.example.modernmessenger.presentation.activity.ChatActivity
import de.hdodenhof.circleimageview.CircleImageView

class ChatsAdapter (
    private val chats : List<Chat>
) : RecyclerView.Adapter<ChatsAdapter.UserViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_text_rv_item, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int = chats.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val chatUser = chats[position].chatUser

        //если у пользователя есть аватарка, то загружаем ее
        if (chatUser.profileImageUrl.isNotEmpty())
            Glide.with(holder.itemView.context).load(chatUser.profileImageUrl).into(holder.chatIv)

        holder.chatNameTv.text = chatUser.username

        //в случае нажатия на чат мы должны перенести пользователя на страницу чата
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ChatActivity::class.java)
            intent.putExtra(CHILD_ID, chats[position].id)
            holder.itemView.context.startActivity(intent)
        }
    }

    inner class UserViewHolder (item: View) : RecyclerView.ViewHolder(item){
        val chatIv: CircleImageView = item.findViewById<CircleImageView>(R.id.chat_iv)
        val chatNameTv: TextView = item.findViewById<TextView>(R.id.chat_name_tv)
    }
}
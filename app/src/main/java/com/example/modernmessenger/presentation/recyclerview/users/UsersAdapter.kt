package com.example.modernmessenger.presentation.recyclerview.users

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.modernmessenger.R
import com.example.modernmessenger.domain.entity.User
import de.hdodenhof.circleimageview.CircleImageView

class UsersAdapter (
    private val users: List<User>,
    private val onChatCreate: (User) -> Unit //лямбда функция, которая срабатывает при нажатии на человека чтобы создать чат
) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>(){

    //студенты после "фильтровки"
    private var filteredUsers = emptyList<User>()

    init {
        filter("") //по умолчанию загоняем в пустой фильтр, чтобы отобразить все
    }

    fun filter(filter: String){
        //смотрим совпадения либо по имени пользователя, либо по номеру телефона
        filteredUsers = users.filter { it.phoneNumber.contains(filter) || it.username.contains(filter) }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_text_rv_item, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int = filteredUsers.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = filteredUsers[position]

        //загружаем аватарку если есть
        if (user.profileImageUrl.isNotEmpty())
            Glide.with(holder.itemView.context).load(user.profileImageUrl).into(holder.chatIv)

        holder.chatNameTv.text = user.username

        //вызываем лямбда функцию чтобы создать чат
        holder.itemView.setOnClickListener {
            this@UsersAdapter.onChatCreate(user)
        }
    }

    class UserViewHolder (item: View) : RecyclerView.ViewHolder(item){
        val chatIv: CircleImageView = item.findViewById(R.id.chat_iv)
        val chatNameTv: TextView = item.findViewById(R.id.chat_name_tv)
    }
}
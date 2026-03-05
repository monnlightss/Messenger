package com.example.modernmessenger.domain.namespaces

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

/** Файл с константами для работы с Firebase Realtime Database **/

val AUTH = FirebaseAuth.getInstance() //аутентификация
val DB = FirebaseDatabase.getInstance().reference //база данных

const val NODE_USERS = "Users"
const val NODE_CHATS = "Chats"

const val CHILD_CHATS = "chats"
const val CHILD_PHONE_NUMBER = "phone_number"
const val CHILD_PROFILE_IMAGE = "profile_image"
const val CHILD_USERNAME = "username"

const val CHILD_ID = "id"
const val CHILD_USER1 = "user1"
const val CHILD_USER2 = "user2"

const val CHILD_MESSAGES = "messages"

const val CHILD_SENDER_ID = "sender_id"
const val CHILD_TEXT = "text"
const val CHILD_DATE = "date"
package com.example.modernmessenger.domain.namespaces

import com.google.firebase.storage.FirebaseStorage

/** Файл с константами для работы с Firebase Storage (облачное пространство для хранения фотографий, видео и прочего) **/

val STORAGE = FirebaseStorage.getInstance().reference

const val NODE_PROFILE_IMAGES = "profile_images"
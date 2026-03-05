package com.example.modernmessenger.domain.repository

import android.net.Uri
import com.example.modernmessenger.domain.entity.Result

/** Интерфейс для работы с Firebase Storage **/
interface FirebaseStorageRepository {
    fun uploadImage(path: String, filePath: Uri, onFinished: (Result) -> Unit)
}
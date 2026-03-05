package com.example.modernmessenger.data.repositoryImpl

import android.net.Uri
import com.example.modernmessenger.domain.entity.Result
import com.example.modernmessenger.domain.namespaces.STORAGE
import com.example.modernmessenger.domain.repository.FirebaseStorageRepository

/** Класс реализации интерфейса FirebaseStorageRepository, который реализует функцию для загрузки изображения в Firebase Storage и дальнейшнего сохранения в БД **/
class FirebaseStorageRepositoryImpl : FirebaseStorageRepository {
    override fun uploadImage(
        path: String,
        filePath: Uri,
        onFinished: (Result) -> Unit
    ) {
        //обращаемся к Firebase Storage
        STORAGE.child(path)
            .putFile(filePath) //загружаем туда файл
            .addOnSuccessListener {
                //если файл успешно загружен, то получаем ссылку на него
                STORAGE.child(path).downloadUrl
                    .addOnSuccessListener{ uri ->
                        onFinished(Result.Success(uri.toString()))
                    }
                    .addOnFailureListener {
                        // если что-то пошло не так, то возвращаем ошибку
                        onFinished(Result.Error(it.message.toString()))
                    }
            }
            .addOnFailureListener {
                //если что-то пошло не так, то возвращаем ошибку
                onFinished(Result.Error(it.message.toString()))
            }
    }
}
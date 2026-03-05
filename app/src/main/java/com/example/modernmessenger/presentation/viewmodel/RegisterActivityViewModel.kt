package com.example.modernmessenger.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.modernmessenger.domain.entity.Result
import com.example.modernmessenger.domain.namespaces.AUTH
import com.example.modernmessenger.domain.namespaces.CHILD_PROFILE_IMAGE
import com.example.modernmessenger.domain.namespaces.CHILD_USERNAME
import com.example.modernmessenger.domain.namespaces.DB
import com.example.modernmessenger.domain.namespaces.NODE_PROFILE_IMAGES
import com.example.modernmessenger.domain.namespaces.NODE_USERS
import com.example.modernmessenger.domain.repository.FirebaseStorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterActivityViewModel @Inject constructor(
    private val firebaseStorageRepository: FirebaseStorageRepository
) : ViewModel() {
    fun uploadImage(imageUri: Uri, onFinished: (Result) -> Unit) = AUTH.currentUser?.uid?.let{
        val path = "$NODE_PROFILE_IMAGES/$it"

        //все действия необходимо исполнять асинхронно, поэтому используем корутины
        viewModelScope.launch(Dispatchers.IO){
            firebaseStorageRepository.uploadImage(
                path = path,
                filePath = imageUri,
                onFinished = {result ->
                    when (result){
                        is Result.Success<*> -> {
                            val profileImageUrl = result.data.toString()
                            addProfileImageUrlToDb(profileImageUrl = profileImageUrl)
                            onFinished(result)
                        }
                        is Result.Error<*> -> {
                            onFinished(result)
                        }
                    }
                }
            )
        }
    } ?: run{onFinished(Result.Error("Ошибка получения id пользователя, перезайдите в приложение"))}

    /** Функция чтобы добавить ссылку на фотографию профиля человека в его директорию в базе данных **/
    private fun addProfileImageUrlToDb(profileImageUrl: String) = AUTH.currentUser?.uid?.let{
        DB.child(NODE_USERS).child(it).child(CHILD_PROFILE_IMAGE).setValue(profileImageUrl)
    }

    fun register(username: String, onFinished: (Result) -> Unit) = AUTH.currentUser?.uid?.let{
        viewModelScope.launch (Dispatchers.IO) {
            DB.child(NODE_USERS).child(it).child(CHILD_USERNAME).setValue(username)
                .addOnSuccessListener {
                    //по окончании позвращаем результат если все ок, но без каких либо данных, потому что их нет
                    onFinished(Result.Success(null))
                }
                .addOnFailureListener {
                    //при ошибке возвращаем текст сообщения об ошибке
                    onFinished(Result.Error(it.message))
                }
        }
    } ?: run{onFinished(Result.Error("Ошибка получения id пользователя, перезайдите в приложение"))}
}
package com.example.modernmessenger.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.modernmessenger.data.usecase.CreateNewChatUseCase
import com.example.modernmessenger.domain.entity.User
import com.example.modernmessenger.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewChatFragmentViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val createNewChatUseCase: CreateNewChatUseCase
) : ViewModel(){
    val users = MutableLiveData<List<User>>()

    init{
        loadAllUsers()
    }

    private fun loadAllUsers() = viewModelScope.launch(Dispatchers.IO){
        val users = userRepository.loadAllUsers()
        this@NewChatFragmentViewModel.users.postValue(users)
    }

    fun createChat(chatUser: User, onSuccess: (String) -> Unit){
        createNewChatUseCase.execute(
            chatUser = chatUser,
            onSuccess = onSuccess
        )
    }
}
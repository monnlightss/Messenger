package com.example.modernmessenger.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.modernmessenger.domain.entity.Chat
import com.example.modernmessenger.domain.namespaces.AUTH
import com.example.modernmessenger.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserChatsFragmentViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    val chats = MutableLiveData<List<Chat>>()

    //загружаем все чаты пользователя
    private fun loadChats() = AUTH.currentUser?.uid?.let{
        viewModelScope.launch(Dispatchers.IO){
            val chats = userRepository.loadUserChats(id = it)
            this@UserChatsFragmentViewModel.chats.postValue(chats)
        }
    } ?: run{chats.postValue(emptyList())}

    init{
        loadChats() //чаты пользователя загружаются сразу при инициализации viewModel класса
    }
}
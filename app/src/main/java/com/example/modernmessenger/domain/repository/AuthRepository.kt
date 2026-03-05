package com.example.modernmessenger.domain.repository

import com.example.modernmessenger.domain.entity.Answer
import com.example.modernmessenger.domain.entity.LoginState
import com.example.modernmessenger.presentation.activity.LoginActivity
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

/** Репозиторий, отвечающий за систему аутентификации **/
interface AuthRepository {
    suspend fun checkAuth() : LoginState

    fun login(credential: PhoneAuthCredential, onFinished: (Answer) -> Unit)

    fun sendVerificationCode(activity: LoginActivity, phoneNumber: String,
                             callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks,
                             onCodeSent : () -> Unit = {}, onFailure: () -> Unit = {})
}
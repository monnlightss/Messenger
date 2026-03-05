package com.example.modernmessenger.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.modernmessenger.data.util.Validator
import com.example.modernmessenger.domain.entity.Answer
import com.example.modernmessenger.domain.entity.LoginBtnState
import com.example.modernmessenger.domain.entity.LoginState
import com.example.modernmessenger.domain.namespaces.AUTH
import com.example.modernmessenger.domain.namespaces.CHILD_PHONE_NUMBER
import com.example.modernmessenger.domain.namespaces.DB
import com.example.modernmessenger.domain.namespaces.NODE_USERS
import com.example.modernmessenger.domain.repository.AuthRepository
import com.example.modernmessenger.presentation.activity.LoginActivity
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginActivityViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val ans = MutableLiveData<Answer>()
    val loginBtnState = MutableLiveData(LoginBtnState.SEND_CODE)
    var phoneNumber: String? = null
    val loginState = MutableLiveData(LoginState.LOGGED_OUT)

    //проверка отправленного кода и введенного кода
    fun verifyCode(code: String, verificationId: String){
        val credential = PhoneAuthProvider.getCredential(verificationId, code)

        viewModelScope.launch (Dispatchers.IO) {
            authRepository.login(
                credential = credential,
                onFinished = {answer->
                    //если сейчас пользователь будет регистрироваться, то заранее добавляем его номер телефона в БД
                    if (answer == Answer.NEW_USER) addPhoneNumberToDb()

                    ans.postValue(answer)
                }
            )
        }
    }

    //чтобы добавить номер телефона пользователя в его директорию в БД
    private fun addPhoneNumberToDb() = viewModelScope.launch(Dispatchers.IO){
        AUTH.currentUser?.uid?.let {
            DB.child(NODE_USERS).child(it).child(CHILD_PHONE_NUMBER).setValue(phoneNumber)
        }
    }

    //отправка смс с кодом подтверждения
    fun sendVerificationCode(
        activity: LoginActivity,
        phoneNumber: String,
        callbacks: OnVerificationStateChangedCallbacks,
        onCodeSent: () -> Unit,
        onFailure: () -> Unit
    ){
        authRepository.sendVerificationCode(
            activity = activity,
            phoneNumber = phoneNumber,
            callbacks = callbacks,
            onCodeSent = onCodeSent,
            onFailure = onFailure
        )
    }

    /** Возвращает true если номер правильный, false если нет **/
    fun validatePhoneNumber() : Boolean {
        this.phoneNumber?.let {
            return Validator.validatePhoneNumber(it)
        } ?: run{
            return false
        }
    }

    /** Возвращает статус входа в аккаунт (вошел, не вошел, вошел но не зарегистрирован)  **/
    fun checkAuth() = viewModelScope.launch{
        val loginState = authRepository.checkAuth()
        this@LoginActivityViewModel.loginState.postValue(loginState)
    }
}
package com.example.modernmessenger.data.repositoryImpl

import com.example.modernmessenger.domain.namespaces.AUTH
import com.example.modernmessenger.domain.namespaces.DB
import com.example.modernmessenger.domain.namespaces.NODE_USERS
import com.example.modernmessenger.domain.database.getDataOnce
import com.example.modernmessenger.domain.entity.Answer
import com.example.modernmessenger.domain.entity.LoginState
import com.example.modernmessenger.domain.repository.AuthRepository
import com.example.modernmessenger.presentation.activity.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.concurrent.TimeUnit

/** Это implementation для интерфейса, отвечающего за систему авторизации в приложении **/
class AuthRepositoryImpl : AuthRepository {
    //проверяем залогинен ли пользователь
    override suspend fun checkAuth(): LoginState{
        if (AUTH.currentUser==null) return LoginState.LOGGED_OUT

        AUTH.currentUser?.uid?.let{
            val userSnapshot = DB.child(NODE_USERS).child(it).getDataOnce()

            if (userSnapshot.exists()) return LoginState.LOGGED_IN
            return LoginState.REGISTER
        } ?: run{
            return LoginState.LOGGED_OUT
        }
    }

    //функция для входа в аккаунт
    override fun login(credential: PhoneAuthCredential, onFinished: (Answer) -> Unit) {
        AUTH.signInWithCredential(credential)
            .addOnCompleteListener { task->
                if (task.isSuccessful){
                    CoroutineScope(Dispatchers.IO).launch {
                        try{
                            val userSnapshot = DB.child(NODE_USERS).child(task.result.user?.uid!!).getDataOnce()

                            if (userSnapshot.exists())
                                onFinished(Answer.SUCCESS)
                            else
                                onFinished(Answer.NEW_USER)
                        }catch(e: Exception){
                            onFinished(Answer.FAILURE)
                        }
                    }
                }
            }
    }

    override fun sendVerificationCode(
        activity: LoginActivity,
        phoneNumber: String,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks,
        onCodeSent: () -> Unit,
        onFailure: () -> Unit
    ) {
        //делаем try catch на случай если что-то пойдет не так при отправке сообщения
        try{
            val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)

            onCodeSent()
        }catch(e: Exception){
            onFailure()
            e.printStackTrace()
        }
    }
}
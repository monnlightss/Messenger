package com.example.modernmessenger.app

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.HiltAndroidApp

/** Главный класс приложения. Тут происходит активация важных компонентов **/
@HiltAndroidApp //Активируем Dagger Hilt для Dependency Injection
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        //инициализируем Firebase SDK в приложении
        FirebaseApp.initializeApp(this)
    }
}
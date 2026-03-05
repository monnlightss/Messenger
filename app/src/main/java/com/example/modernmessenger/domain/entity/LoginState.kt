package com.example.modernmessenger.domain.entity

/** Класс для определения статуса пользователя в приложении:
 * 1. Залогинен
 * 2. Залогинен, но требуется регистрация в БД
 * 3. Не залогинен
 * **/
enum class LoginState {
    LOGGED_IN, REGISTER, LOGGED_OUT
}
package com.example.modernmessenger.domain.entity

/** Ответ системы авторизации:
 * 1. Вход успешно
 * 2. Вход успешно, но пользователь не зарегистрирован
 * 3. Ошибка входа
 * **/
enum class Answer {
    SUCCESS, NEW_USER, FAILURE
}
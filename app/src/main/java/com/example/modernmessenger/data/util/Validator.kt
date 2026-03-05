package com.example.modernmessenger.data.util

object Validator {
    /** Возвращает true если номер телефона в правильном формате (для Firebase) **/
    fun validatePhoneNumber(phoneNumber : String) : Boolean{
        // регулярное выражение для номера телефона без пробелов, (, ), -
        val pattern = Regex("^\\+[0-9]{10,14}\$")

        // проверяем соответствует ли номер телефона паттерну
        return pattern.matches(phoneNumber)
    }
}
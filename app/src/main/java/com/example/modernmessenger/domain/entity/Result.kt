package com.example.modernmessenger.domain.entity

/** Класс, отображающий результат выполнения какой-либо функции **/
sealed class Result {
    data class Success<T> (val data: T) : Result()
    data class Error<T> (val error: T) : Result()
}
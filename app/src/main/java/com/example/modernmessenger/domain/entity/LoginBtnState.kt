package com.example.modernmessenger.domain.entity

/** У кнопки на странице входа есть два состояния:
 * 1. Код не отправлен, по нажатии на кнопку отправляется код - SEND_CODE
 * 2. Код отправлен, по нажатии на кнопку происходит вход в аккаунт - LOGIN
 * **/
enum class LoginBtnState {
    SEND_CODE, LOGIN
}
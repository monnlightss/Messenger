package com.example.modernmessenger

import com.example.modernmessenger.data.util.Validator
import org.junit.Assert.assertEquals
import org.junit.Test

class PhoneNumberValidatorTests {
    @Test
    fun standartPhone(){
        val phoneNumber = "+79899736007"
        assertEquals(true, Validator.validatePhoneNumber(phoneNumber = phoneNumber))
    }

    @Test
    fun wrongPhoneNumber1(){
        val phoneNumber = "89899736007"
        assertEquals(false, Validator.validatePhoneNumber(phoneNumber = phoneNumber))
    }

    @Test
    fun wrongPhoneNumber2(){
        val phoneNumber = "-89899736007"
        assertEquals(false, Validator.validatePhoneNumber(phoneNumber = phoneNumber))
    }
}
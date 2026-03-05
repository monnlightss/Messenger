package com.example.modernmessenger.data.util

import java.util.Arrays

object Str{
    //сортировка строки по алфавиту
    fun sort(str: String) : String{
        val chars = str.toCharArray()
        Arrays.sort(chars)
        return String(chars)
    }
}
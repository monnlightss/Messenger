package com.example.modernmessenger.presentation.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.example.modernmessenger.databinding.LoadingAlertDialogBinding

/** Кастомный диалог загрузки **/
class LoadingAlertDialog(context: Context, val message: String = "Загрузка...") : Dialog(context) {

    private lateinit var binding: LoadingAlertDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoadingAlertDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loadingMsgTv.text = message
    }
}
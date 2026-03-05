package com.example.modernmessenger.presentation.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.modernmessenger.databinding.ActivityRegisterBinding
import com.example.modernmessenger.domain.entity.Result
import com.example.modernmessenger.presentation.fragment.LoadingAlertDialog
import com.example.modernmessenger.presentation.viewmodel.RegisterActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint //аннотация @AndroidEntryPoint означает, что в этом компоненте (в данном случае activity) используется DI с помощью Dagger Hilt
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val viewModel: RegisterActivityViewModel by viewModels()

    private lateinit var loadingAlertDialog: LoadingAlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingAlertDialog = LoadingAlertDialog(this, "Подождите, отправляем вашу фотографию")
        loadingAlertDialog.setCancelable(false)

        binding.profileIv.setOnClickListener {
            //из-за определенных изменений после версии Android API 33 (Tiramisu) у нас для разных версий есть разные названия разрешений
            val readImagePermission: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES // для более новых версий Android
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE // для более старых версий Android
            }

            //смотрим если у нас уже есть разрешение на чтение галереи
            if (ContextCompat.checkSelfPermission(
                    baseContext,
                    readImagePermission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestGalleryPermission.launch(readImagePermission)
            } else {
                openGalleryPicker()
            }
        }

        binding.signUpBtn.setOnClickListener {
            val username = binding.usernameEt.text.toString()

            if (username.isEmpty()){
                binding.usernameEt.error = "Введите имя пользователя"
                binding.usernameEt.requestFocus()
                return@setOnClickListener
            }
            loadingAlertDialog.show()

            viewModel.register(
                username = username
            ){result ->
                loadingAlertDialog.dismiss()

                when (result) {
                    //если все ок, то мы должны получить Result.Success с полученными данными внутри
                    is Result.Success<*> -> {
                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                    //если возникла ошибка, то получаем сообщение о ней
                    is Result.Error<*> -> {
                        val message = result.error.toString()
                        Toast.makeText(this, "Ошибка: $message", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun uploadImage(imageUri: Uri){
        viewModel.uploadImage(
            imageUri = imageUri
        ){result->
            // прячем диалог загрузки
            loadingAlertDialog.dismiss()

            // проверяем какой результат мы получили: успешно или ошибка
            when (result) {
                is Result.Success<*> -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Фотография успешно загружена",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Result.Error<*> -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Ошибка загрузки фотографии: ${result.error}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun openGalleryPicker(){
        //создаем специальный intent, для того чтобы открыть галерею
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        pickImageResultLauncher.launch(intent)
    }

    private val requestGalleryPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {isGranted->
        if (isGranted){
            //если мы уже получили разрешение на галерею, то просто ее открываем
            openGalleryPicker()
        }else{
            Toast.makeText(
                this@RegisterActivity,
                "Нет разрешения на доступ к галерее. Проверьте настройки",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val pickImageResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == RESULT_OK && it.data!=null && it.data?.data!=null){ //должны проверить что нигде не получили пустой результат
            val imagePath = it.data!!.data!!
            binding.profileIv.setImageURI(imagePath)

            loadingAlertDialog.show()

            uploadImage(imageUri = imagePath)
        }else{
            Toast.makeText(
                this@RegisterActivity,
                "Нет разрешения на доступ к галерее. Проверьте настройки",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
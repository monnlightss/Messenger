package com.example.modernmessenger.presentation.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.example.modernmessenger.databinding.ActivityLoginBinding
import com.example.modernmessenger.domain.entity.Answer
import com.example.modernmessenger.domain.entity.LoginBtnState
import com.example.modernmessenger.domain.entity.LoginState
import com.example.modernmessenger.domain.namespaces.PHONE_NUMBER_EXTRA
import com.example.modernmessenger.presentation.fragment.LoadingAlertDialog
import com.example.modernmessenger.presentation.viewmodel.LoginActivityViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginActivityViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding
    private var verificationid: String? = null
    private lateinit var loadingBar: LoadingAlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //инициализируем диалог загрузки
        loadingBar = LoadingAlertDialog(this)

        checkAuth()

        loginBtnStateListener()

        binding.actionBtn.setOnClickListener {
            //получаем введенный номер телефона
            viewModel.phoneNumber = binding.phoneEt.text.toString()

            //проверяем правильно ли отформатирован номер телефона и введен ли он вообще
            if (!viewModel.validatePhoneNumber()){
                Toast.makeText(
                    this@LoginActivity,
                    "Ошибка: номер телефона не указан или введен в неверном формате.",
                    Toast.LENGTH_SHORT
                ).show()
                binding.phoneEt.requestFocus() //обращаем внимание пользователя на поле ввода телефона

                return@setOnClickListener
            }

            //наша кнопка может быть в двух состояниях: когда смс код еще не отправлен и когда отправлен
            when(viewModel.loginBtnState.value){
                LoginBtnState.LOGIN -> {
                    login()
                }
                LoginBtnState.SEND_CODE -> {
                    //пока отправляем код показываем пользователю, что что-то загружается
                    loadingBar.show()

                    sendCode()
                }
                null -> {}
            }
        }
    }

    private fun checkAuth(){
        loadingBar.show()
        viewModel.loginState.observe(this){loginState->
            loginState?.let {
                loadingBar.dismiss()
                when(loginState){
                    LoginState.LOGGED_IN -> {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    //если пользователь успешно вошел в аккаунт, но не зарегистрирован в базе данных
                    LoginState.REGISTER -> {
                        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    LoginState.LOGGED_OUT -> {}
                }
            }
        }
        viewModel.checkAuth()
    }

    private fun login(){
        val code = binding.codeEt.text.toString().trim()

        //код всегда длиной 6 цифр или больше
        if (code.length < 6){
            binding.codeEt.error = "Введите код"
            binding.codeEt.requestFocus()
            return
        }

        //если код верификации не null, тогда мы можем его проверить
        verifyCode(code = code)
    }

    private fun sendCode(){
        //отправляем смс
        viewModel.phoneNumber?.let {
            viewModel.sendVerificationCode(
                activity = this,
                phoneNumber = it,
                callbacks = mCallBack,
                onCodeSent = {
                    loadingBar.dismiss()

                    viewModel.loginBtnState.postValue(LoginBtnState.LOGIN)
                    binding.codeEt.isEnabled = true //до отправки кода поле для его ввода заблокировано, чтобы избежать непонимания
                },
                onFailure = {
                    Toast.makeText(
                        this,
                        "Ошибка",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

    private fun verifyCode(code: String){
        verificationid?.let{viewModel.verifyCode(code, it)}
        viewModel.phoneNumber?.let{phoneNumber->
            viewModel.ans.observe(this@LoginActivity){ans->
                ans?.let {
                    when (ans){
                        //если пользователь есть в БД и ему не надо регистрироваться
                        Answer.SUCCESS -> {
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        //если пользователя еще нет в БД и ему надо зарегистрироваться
                        Answer.NEW_USER -> {
                            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                            intent.putExtra(PHONE_NUMBER_EXTRA, phoneNumber)
                            startActivity(intent)
                        }
                        //в случае ошибки входа
                        Answer.FAILURE -> {
                            Toast.makeText(
                                this@LoginActivity,
                                "Ошибка, проверьте правильность ввода номера телефона и кода",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

    }

    private fun loginBtnStateListener(){
        /* Смотрим на состояние кнопки входа: если код уже отправлен,
        то она должна иметь action входа в аккаунт, если не отправлен, то action отправки кода
        Текст кнопки должен быть соответствующий.
         */
        viewModel.loginBtnState.observe(this){state->
            binding.actionBtn.text = if (state == LoginBtnState.SEND_CODE)
                "Получить код"
            else "Войти"
        }
    }

    private val mCallBack: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                verificationid = p0
            }

            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                //получаем код из смс
                val code = p0.smsCode
                code?.let{
                    binding.codeEt.setText(code)

                    verifyCode(code = code)
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(
                    this@LoginActivity,
                    "Ошибка: ${p0.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
}
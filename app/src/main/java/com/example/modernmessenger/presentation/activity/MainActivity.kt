package com.example.modernmessenger.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.modernmessenger.R
import com.example.modernmessenger.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //настраиваем нижнюю панель навигации
        val navHostFragment = (supportFragmentManager.findFragmentById(R.id.main_fragment) as NavHostFragment?)!!
        val navController = navHostFragment.navController
        setupWithNavController(binding.bottomNav, navController)
    }
}
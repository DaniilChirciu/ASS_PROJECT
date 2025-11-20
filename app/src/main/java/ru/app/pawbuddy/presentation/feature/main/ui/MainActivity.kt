package ru.app.pawbuddy.presentation.feature.main.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.app.pawbuddy.databinding.ActivityMainBinding
import ru.app.pawbuddy.presentation.util.replaceFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        replaceFragment(MainFragment())



    }
}
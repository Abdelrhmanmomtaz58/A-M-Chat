package com.momtaz.amchat.Activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.momtaz.amchat.R
import com.momtaz.amchat.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
    }

    private fun setListeners() {
        binding.textSignIn.setOnClickListener { v -> onBackPressedDispatcher.onBackPressed()}
    }
}
package com.momtaz.amchat.Activites

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.momtaz.amchat.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
    }

    private fun setListeners() {
        binding.textCreateNweAccount.setOnClickListener {
            startActivity(Intent(this@SignInActivity,SignUpActivity::class.java))
        }
    }
}
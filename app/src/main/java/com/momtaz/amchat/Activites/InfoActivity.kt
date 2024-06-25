package com.momtaz.amchat.Activites

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import com.google.firebase.firestore.FirebaseFirestore
import com.momtaz.amchat.databinding.ActivityInfoBinding
import com.momtaz.amchat.utilities.Constants
import com.momtaz.amchat.utilities.PreferenceManager

class InfoActivity : AppCompatActivity() {
    lateinit var binding:ActivityInfoBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var db:FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityInfoBinding.inflate(layoutInflater)
        db=FirebaseFirestore.getInstance()
        preferenceManager= PreferenceManager(applicationContext)
        setContentView(binding.root)
        setInfo()
        callBack()
    }

    private fun callBack() {
        binding.imageBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setInfo() {
        binding.textName.text=preferenceManager.getString(Constants.KEY_NAME)
        binding.textEmail.text=preferenceManager.getString(Constants.KEY_EMAIL)
        val byte = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(byte, 0, byte.size)
        binding.imageProfile.setImageBitmap(bitmap)
    }
}
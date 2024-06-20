package com.momtaz.amchat.Activites

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.momtaz.amchat.databinding.ActivityMainBinding
import com.momtaz.amchat.utilities.Constants
import com.momtaz.amchat.utilities.PreferenceManager

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        loadUserDetails()
        getToken()
        setListeners()
    }
    private fun setListeners(){
        binding.imageSignOut.setOnClickListener {
            signOut()
        }
        binding.fabNewChat.setOnClickListener {
            startActivity(Intent(applicationContext,UsersActivity::class.java))
        }
    }
    private fun loadUserDetails(){
        binding.textName.text = preferenceManager.getString(Constants.KEY_NAME)
        val byte = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE),Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(byte,0,byte.size)
        binding.imageProfile.setImageBitmap(bitmap)

    }
    private fun showToast(message:String){
        Toast.makeText(applicationContext,message, Toast.LENGTH_LONG).show()
    }
    private fun getToken(){
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token -> updateToken(token) }
    }
    private fun updateToken(token:String){
        val database = FirebaseFirestore.getInstance()
        val documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
            .document(preferenceManager.getString(Constants.KEY_USER_ID).toString())
        documentReference.update(Constants.KEY_FCM_TOKEN,token)
            .addOnFailureListener { showToast("unable to update token") }
    }
    private fun signOut(){
        showToast("signing out.....")
        val db =FirebaseFirestore.getInstance()
        val documentReference =
            db.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID).toString()
            )
        var update = mutableMapOf<String,Any>()
        update.put(Constants.KEY_FCM_TOKEN,FieldValue.delete())
        documentReference.update(update)
            .addOnSuccessListener {
                preferenceManager.clear()
                startActivity(Intent(applicationContext,SignInActivity::class.java))
                finish()
            }.addOnFailureListener { showToast("unable to sign out") }
    }
}
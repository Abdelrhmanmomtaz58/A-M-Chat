package com.momtaz.amchat.Activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.momtaz.amchat.databinding.ActivitySignInBinding
import com.momtaz.amchat.utilities.Constants
import com.momtaz.amchat.utilities.PreferenceManager


class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            val intent = Intent(applicationContext,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        setListeners()
    }

    private fun setListeners() {
        binding.textCreateNweAccount.setOnClickListener {
            startActivity(Intent(this@SignInActivity,SignUpActivity::class.java))
        }
        binding.buttonSignIn.setOnClickListener {
            if (isValidSignInDetails())
            {
                signIn()
            }
        }
    }
    private fun showToast(message:String){
        Toast.makeText(applicationContext,message, Toast.LENGTH_LONG).show()
    }
    private fun signIn()
    {
        loading(true)
        val database = FirebaseFirestore.getInstance()
        database.collection(Constants.KEY_COLLECTION_USERS)
            .whereEqualTo(Constants.KEY_EMAIL,binding.inputEmail.text.toString())
            .whereEqualTo(Constants.KEY_PASSWORD,binding.inputPassword.text.toString())
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result !=null && task.result.documents.size>0){
                    val documentSnapShot = task.result.documents[0]
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true)
                    preferenceManager.putString(Constants.KEY_USER_ID,documentSnapShot.id)
                    preferenceManager.putString(Constants.KEY_NAME,documentSnapShot.getString(Constants.KEY_NAME).toString())
                    preferenceManager.putString(Constants.KEY_IMAGE,documentSnapShot.getString(Constants.KEY_IMAGE).toString())
                    preferenceManager.putString(Constants.KEY_EMAIL,documentSnapShot.getString(Constants.KEY_EMAIL).toString())
                    val intent = Intent(applicationContext,MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }else{
                    loading(false)
                    showToast("Unable to sign in")
                }
            }

    }
    private fun loading(isLoading:Boolean)
    {
        if (isLoading){
            binding.buttonSignIn.visibility=View.INVISIBLE
            binding.progressBar.visibility=View.VISIBLE
        }else{
            binding.buttonSignIn.visibility=View.VISIBLE
            binding.progressBar.visibility=View.INVISIBLE
        }
    }
    private fun isValidSignInDetails(): Boolean {
        return if (binding.inputEmail.text.toString().trim().isEmpty()) {
            showToast("Enter Email")
            false
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches()) {
            showToast("Enter valid Email")
            false
        }else if(binding.inputPassword.text.toString().trim().isEmpty()){
            showToast("Enter password")
            false
        }else{
            true
        }
    }
}
package com.momtaz.amchat.Activites

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.firestore.FirebaseFirestore
import com.momtaz.amchat.databinding.ActivitySignUpBinding
import com.momtaz.amchat.utilities.Constants
import com.momtaz.amchat.utilities.PreferenceManager
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    lateinit var preferenceManager: PreferenceManager
    var encodedImage:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        setListeners()
    }

    private fun setListeners() {
        binding.textSignIn.setOnClickListener { onBackPressedDispatcher.onBackPressed()}
        binding.buttonSignUp.setOnClickListener {
            if (isValidSignUpDetails()){
                signUp()
            }
        }
        binding.layoutImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImage.launch(intent)
        }

    }
    private fun showToast(message:String){
       Toast.makeText(applicationContext,message,Toast.LENGTH_LONG).show()
   }
    private fun signUp(){
        loading(true)
        val database = FirebaseFirestore.getInstance()
        val user = mutableMapOf<String,Any>()
        user.put(Constants.KEY_NAME,binding.inputName.text.toString())
        user.put(Constants.KEY_EMAIL,binding.inputEmail.text.toString())
        user.put(Constants.KEY_PASSWORD,binding.inputPassword.text.toString())
        user.put(Constants.KEY_IMAGE, encodedImage!!)
        database.collection(Constants.KEY_COLLECTION_USERS)
            .add(user)
            .addOnSuccessListener {documentReference->
                loading(false)
                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true)
                preferenceManager.putString(Constants.KEY_USER_ID,documentReference.id)
                preferenceManager.putString(Constants.KEY_NAME,binding.inputName.text.toString())
                preferenceManager.putString(Constants.KEY_IMAGE, encodedImage!!)
                val intent=Intent(applicationContext,MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or  Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)


            }.addOnFailureListener {exceptoin ->
                loading(false)
                showToast(exceptoin.message.toString())
            }


    }
    private fun encodeImage(bitmap: Bitmap):String{
        val previewWidth =150
        val previewHeight = bitmap.height * previewWidth / bitmap.width
        val previewBitmap =Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false)
        val byteArrayOutPutStream = ByteArrayOutputStream()
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutPutStream)
        val bytes = byteArrayOutPutStream.toByteArray()
        return Base64.encodeToString(bytes,Base64.DEFAULT)
    }
    private val pickImage : ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
        if (result.resultCode== RESULT_OK)
        {
            if (result.data!=null){
                val imageUri: Uri? = result.data?.data
                try {
                    val inputStream = contentResolver.openInputStream(imageUri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    binding.imageProfile.setImageBitmap(bitmap)
                    binding.textAddImage.visibility = View.GONE
                    encodedImage = encodeImage(bitmap)
                }catch (e : FileNotFoundException){
                    e.printStackTrace()
                }
            }
        }
    }
    private fun isValidSignUpDetails():Boolean{
        if (encodedImage==null){
           showToast("Select profile image")
            return false
        }else if(binding.inputName.text.toString().trim().isEmpty())
        {
            showToast("Enter your name")
            return false

        }else if (binding.inputEmail.text.toString().trim().isEmpty()){
           showToast("Enter your Email")
            return false

        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches())
        {
           showToast("Enter valid Email")
            return false
        }else if (binding.inputPassword.text.toString().trim().isEmpty()){
           showToast("Enter password")
            return false
        }else if (binding.inputConfirmPassword.text.toString().trim()!=binding.inputPassword.text.toString().trim()){
           showToast("password and confirm password are no matching")
            return false

        }else if (binding.inputConfirmPassword.text.toString().trim().isEmpty()){
           showToast("Enter confirm password")
            return false

        }else{
            return true
        }

    }
    private fun loading(isLoading :Boolean){
        if (isLoading){
            binding.buttonSignUp.visibility = View.INVISIBLE
            binding.progressBar.visibility=View.VISIBLE
        }else{
            binding.progressBar.visibility=View.INVISIBLE
            binding.buttonSignUp.visibility=View.VISIBLE
        }

    }
}
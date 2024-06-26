package com.momtaz.amchat.Activites

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import com.momtaz.amchat.adapters.UsersAdapter
import com.momtaz.amchat.databinding.ActivityUsersBinding
import com.momtaz.amchat.listenrs.UserListener
import com.momtaz.amchat.models.User
import com.momtaz.amchat.utilities.Constants
import com.momtaz.amchat.utilities.PreferenceManager

class UsersActivity : BaseActivity(),UserListener {
    private lateinit var binding: ActivityUsersBinding
    private lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        getUsers()
        setListeners()
    }
    private fun setListeners(){
        binding.imageBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.addFriend.setOnClickListener { startActivity(Intent(applicationContext,SearchActivity::class.java)) }
    }
    private fun getUsers(){
        loading(true)
        val currentUserId = preferenceManager.getString(Constants.KEY_USER_ID)
        val database = FirebaseFirestore.getInstance()
        database.collection(Constants.KEY_COLLECTION_USERS).document(currentUserId!!).collection(Constants.KEY_FRIENDS)
            .get()
            .addOnCompleteListener { task ->
                loading(false)

                if (task.isSuccessful&&task.result!=null){
                    val users = ArrayList<User>()
                   for (queryDocumentSnapshot in task.result){
                       if (currentUserId == queryDocumentSnapshot.id){
                           continue
                       }
                       val user = User().apply {
                           name = queryDocumentSnapshot.getString(Constants.KEY_NAME).toString()
                           email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL).toString()
                           image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE).toString()
                           token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN).toString()
                           id = queryDocumentSnapshot.id
                       }


                       users.add(user)
                   }
                    if (users.size>0){
                        val usersAdapter =UsersAdapter(users,this)
                        binding.usersRecyclerView.adapter =usersAdapter
                        binding.usersRecyclerView.visibility=View.VISIBLE
                    }else{
                        binding.enp.text="There are no friends yet\nadd some friends"
                        binding.enp.visibility=View.VISIBLE
                        showErrorMessage()
                    }
                }else{
                    showErrorMessage()
                }
            }
    }
    private fun showErrorMessage(){
        binding.textErrorMessage.text = String().format("%s","No user available")
        binding.textErrorMessage.visibility =View.VISIBLE
    }

    private fun loading(isLoading :Boolean){
        if (isLoading){
            binding.progressBar.visibility =View.VISIBLE
        }else{
            binding.progressBar.visibility =View.INVISIBLE
        }
    }

    override fun onUserClicked(user: User) {
        val intent = Intent(applicationContext, ChatActivity::class.java)
        intent.putExtra(Constants.KEY_USER, user)
        startActivity(intent)
        finish()
    }
}
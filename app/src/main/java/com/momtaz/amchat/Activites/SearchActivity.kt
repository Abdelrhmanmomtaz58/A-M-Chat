package com.momtaz.amchat.Activites


import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.momtaz.amchat.R
import com.momtaz.amchat.adapters.SearchUserAdapter
import com.momtaz.amchat.databinding.ActivitySearchBinding
import com.momtaz.amchat.listenrs.UserListener
import com.momtaz.amchat.models.User
import com.momtaz.amchat.utilities.Constants
import com.momtaz.amchat.utilities.PreferenceManager

class SearchActivity : BaseActivity(),UserListener {
    lateinit var binding:ActivitySearchBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var userAdapter: SearchUserAdapter
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var auth: FirebaseAuth
    private val userList = mutableListOf<User>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loading(false)
        preferenceManager = PreferenceManager(applicationContext)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val recyclerView: RecyclerView = findViewById(R.id.usersRecyclerView)
        userAdapter = SearchUserAdapter(userList,this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = userAdapter
        setListeners()
    }

    private fun setListeners() {
        binding.inputEmail.setOnEditorActionListener { _, actionId, _ ->
            loading(true)
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val emailToSearch = binding.inputEmail.text.toString().trim()
                if (emailToSearch.isNotEmpty()) {
                    searchUsersByEmail(emailToSearch)
                } else {
                    Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }
        binding.imageBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
    private fun searchUsersByEmail(email: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.textErrorMessage.visibility = View.GONE

        db.collection(Constants.KEY_COLLECTION_USERS)
            .whereEqualTo(Constants.KEY_EMAIL, email)
            .get()
            .addOnSuccessListener { documents ->
                loading(false)
                userList.clear()
                for (document in documents) {
                    val user = User().fromDocument(document)
                    userList.add(user)
                }
                userAdapter.notifyDataSetChanged()
                binding.progressBar.visibility = View.GONE
                if (documents.isEmpty) {
                    binding.textErrorMessage.visibility = View.VISIBLE
                    binding.textErrorMessage.text = "No users found with this email"
                }
            }
            .addOnFailureListener { exception ->
                binding.progressBar.visibility = View.GONE
                binding.textErrorMessage.visibility = View.VISIBLE
                binding.textErrorMessage.text = "Error getting users: ${exception.message}"
                Toast.makeText(this, "Error getting users", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loading(isLoading :Boolean){
        if (isLoading){
            binding.progressBar.visibility =View.VISIBLE
        }else{
            binding.progressBar.visibility =View.INVISIBLE
        }
    }

    override fun onUserClicked(user: User) {
        addAsFriend(user)
    }

    private fun addAsFriend(user: User) {
        val currentUserId = preferenceManager.getString(Constants.KEY_USER_ID)
        Log.d("AddFriend", "currentUserId: $currentUserId")
        Log.d("AddFriend", "user.id: ${user.id}")
        if (currentUserId != user.id) {
            if (currentUserId != null) {
                if (user.id != null) {
                    val friendRef = db.collection(Constants.KEY_COLLECTION_USERS)
                        .document(currentUserId)
                        .collection(Constants.KEY_FRIENDS)
                        .document(user.id!!)

                    friendRef.get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                Toast.makeText(
                                    this,
                                    "${user.name} is already your friend",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                friendRef.set(user)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this,
                                            "${user.name} added to friends",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            this,
                                            "Failed to add friend: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "Error checking friend status: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    Toast.makeText(this, "User ID is null for ${user.name}", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(applicationContext,"You cannot add yourself !!",Toast.LENGTH_LONG).show()
        }
    }



}
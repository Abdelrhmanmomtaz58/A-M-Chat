package com.momtaz.amchat.Activites

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.messaging.FirebaseMessaging
import com.momtaz.amchat.adapters.RecentConversationsAdapter
import com.momtaz.amchat.databinding.ActivityMainBinding
import com.momtaz.amchat.listenrs.ConversionListener
import com.momtaz.amchat.models.ChatMessage
import com.momtaz.amchat.models.User
import com.momtaz.amchat.utilities.Constants
import com.momtaz.amchat.utilities.PreferenceManager
import java.util.Date

class MainActivity : BaseActivity(),ConversionListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var conversations: ArrayList<ChatMessage>
    private lateinit var conversationsAdapter: RecentConversationsAdapter
    private lateinit var database: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        init()
        loadUserDetails()
        getToken()
        setListeners()
        listenConversations()
    }

    private fun init() {
        conversations = ArrayList()
        conversationsAdapter = RecentConversationsAdapter(conversations,this)
        binding.conversationsRecyclerView.adapter = conversationsAdapter
        database = FirebaseFirestore.getInstance()
    }

    private fun setListeners() {
        binding.imageSignOut.setOnClickListener {
            signOut()
        }
        binding.fabNewChat.setOnClickListener {
            startActivity(Intent(applicationContext, UsersActivity::class.java))
        }
    }

    private fun loadUserDetails() {
        binding.textName.text = preferenceManager.getString(Constants.KEY_NAME)
        val byte = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(byte, 0, byte.size)
        binding.imageProfile.setImageBitmap(bitmap)

    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }
    private fun listenConversations(){
        database.collection(Constants.KEY_Collection_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)
        database.collection(Constants.KEY_Collection_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_RECEIVER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)
    }
    private val eventListener = EventListener<QuerySnapshot> { value, error ->
        if (error != null) {
            return@EventListener
        }
        if (value != null) {
            for (documentChange in value.documentChanges) {
                if (documentChange.type == DocumentChange.Type.ADDED) {
                    val senderId = documentChange.document.getString(Constants.KEY_SENDER_ID)
                    val receiverId = documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                    val chatMessage = ChatMessage()
                    chatMessage.senderId = senderId
                    chatMessage.receiverId = receiverId
                    if (preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)) {
                        chatMessage.conversionImage=documentChange.document.getString(Constants.KEY_RECEIVER_IMAGE)
                        chatMessage.conversionName=documentChange.document.getString(Constants.KEY_RECEIVER_NAME)
                        chatMessage.conversionId=documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                    }else{
                        chatMessage.conversionImage=documentChange.document.getString(Constants.KEY_SENDER_IMAGE)
                        chatMessage.conversionName=documentChange.document.getString(Constants.KEY_SENDER_NAME)
                        chatMessage.conversionId=documentChange.document.getString(Constants.KEY_SENDER_ID)
                    }
                    chatMessage.message=documentChange.document.getString(Constants.KEY_LAST_MESSAGE)
                    chatMessage.dataObject=documentChange.document.getDate(Constants.KEY_TIMESTAMP)
                    conversations.add(chatMessage)
                }else if (documentChange.type==DocumentChange.Type.MODIFIED){
                    for (i in conversations.indices) {
                        val senderId = documentChange.document.getString(Constants.KEY_SENDER_ID)
                        val receiverId = documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                        if (conversations[i].senderId == senderId && conversations[i].receiverId == receiverId) {
                            conversations[i].message = documentChange.document.getString(Constants.KEY_LAST_MESSAGE) ?: ""
                            conversations[i].dataObject = documentChange.document.getDate(Constants.KEY_TIMESTAMP) ?: Date()
                            break
                        }
                    }
                }

            }
            conversations.sortWith{ obj1, obj2 ->
                obj2.dataObject?.compareTo(obj1.dataObject) ?:0
            }
            conversationsAdapter.notifyDataSetChanged()
            binding.conversationsRecyclerView.smoothScrollToPosition(0)
            binding.conversationsRecyclerView.visibility=View.VISIBLE
            binding.progressBar.visibility=View.GONE

        }
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token -> updateToken(token)
        }
    }

    private fun updateToken(token: String) {
        preferenceManager.putString(Constants.KEY_FCM_TOKEN,token)
        val database = FirebaseFirestore.getInstance()
        Log.i("Token",token.toString())
        val documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
            .document(preferenceManager.getString(Constants.KEY_USER_ID).toString())
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
            .addOnFailureListener { showToast("unable to update token") }
    }

    private fun signOut() {
        showToast("signing out.....")
        val db = FirebaseFirestore.getInstance()
        val documentReference =
            db.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID).toString()
            )
        val update = mutableMapOf<String, Any>()
        update[Constants.KEY_FCM_TOKEN] = FieldValue.delete()
        documentReference.update(update)
            .addOnSuccessListener {
                preferenceManager.clear()
                startActivity(Intent(applicationContext, SignInActivity::class.java))
                finish()
            }.addOnFailureListener { showToast("unable to sign out") }
    }

    override fun onConversionClicked(user: User) {
        val intent = Intent(applicationContext,ChatActivity::class.java)
        intent.putExtra(Constants.KEY_USER,user)
        startActivity(intent)
    }
}
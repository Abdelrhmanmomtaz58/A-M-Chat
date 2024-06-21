package com.momtaz.amchat.Activites

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.momtaz.amchat.adapters.ChatAdapter
import com.momtaz.amchat.databinding.ActivityChatBinding
import com.momtaz.amchat.models.ChatMessage
import com.momtaz.amchat.models.User
import com.momtaz.amchat.utilities.Constants
import com.momtaz.amchat.utilities.PreferenceManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatActivity : AppCompatActivity() {
    private lateinit var binding :ActivityChatBinding
    private lateinit var receiverUser :User
    private lateinit var chatMessages :ArrayList<ChatMessage>
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var database:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadReceiverDetails()
        setListeners()
        init()
        listenMessage()
    }
    private fun init(){
        preferenceManager = PreferenceManager(applicationContext)
        chatMessages = ArrayList()
        chatAdapter = ChatAdapter(
            chatMessages,
            getBitmapFromEncodedString(receiverUser.image),
            preferenceManager.getString(Constants.KEY_USER_ID)
        )
        binding.chatRecyclerView.adapter=chatAdapter
        database=FirebaseFirestore.getInstance()

    }
    private fun listenMessage(){
        database.collection(Constants.KEy_COLLECTION_CHAT)
            .whereEqualTo(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
            .whereEqualTo(Constants.KEY_RECEIVER_ID,receiverUser.id)
            .addSnapshotListener(eventListener)
        database.collection(Constants.KEy_COLLECTION_CHAT)
            .whereEqualTo(Constants.KEY_SENDER_ID,receiverUser.id)
            .whereEqualTo(Constants.KEY_RECEIVER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)
    }
    private val eventListener = EventListener<QuerySnapshot> { value, error ->
        if (error != null) {
            return@EventListener
        }
        if (value != null) {
            val initialCount = chatMessages.size
            for (documentChange in value.documentChanges) {
                if (documentChange.type == DocumentChange.Type.ADDED) {
                    val chatMessage = ChatMessage(
                        senderId = documentChange.document.getString(Constants.KEY_SENDER_ID)!!,
                        receiverId = documentChange.document.getString(Constants.KEY_RECEIVER_ID)!!,
                        message = documentChange.document.getString(Constants.KEY_MESSAGE)!!,
                        dateTime = getReadableDateTime(documentChange.document.getDate(Constants.KEY_TIMESTAMP)!!),
                        dataObject = documentChange.document.getDate(Constants.KEY_TIMESTAMP)!!
                    )
                    chatMessages.add(chatMessage)
                }
            }
            chatMessages.sortBy { it.dataObject }

            if (initialCount == 0) {
                chatAdapter.notifyDataSetChanged()
            } else {
                chatAdapter.notifyItemRangeInserted(initialCount, chatMessages.size - initialCount)
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size - 1)
            }

            binding.chatRecyclerView.visibility = View.VISIBLE
        }
        binding.progressBar.visibility = View.GONE
    }

    private fun sendMessage(){
        val message = HashMap<String,Any>()
        message[Constants.KEY_SENDER_ID] = preferenceManager.getString(Constants.KEY_USER_ID)!!
        message[Constants.KEY_RECEIVER_ID] = receiverUser.id!!
        message[Constants.KEY_MESSAGE] = binding.inputMessage.text.toString()
        message[Constants.KEY_TIMESTAMP] = Date()
        database.collection(Constants.KEy_COLLECTION_CHAT).add(message)
        binding.inputMessage.text=null

    }
    private fun getBitmapFromEncodedString(encodedImage: String?):Bitmap{
        val bytes = Base64.decode(encodedImage,Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes,0,bytes.size)
    }
    private fun loadReceiverDetails(){
        receiverUser = intent.getSerializableExtra(Constants.KEY_USER) as User
        binding.textName.text=receiverUser.name

    }
    private fun getReadableDateTime(data:Date):String{
        return SimpleDateFormat("MMMM dd, yyy - hh:mm a",Locale.getDefault()).format(data)
    }
    private fun setListeners(){
        binding.imageBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.layoutSend.setOnClickListener { sendMessage() }
    }
}
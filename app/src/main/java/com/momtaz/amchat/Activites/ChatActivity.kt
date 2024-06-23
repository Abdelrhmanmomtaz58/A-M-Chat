package com.momtaz.amchat.Activites

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import com.google.android.gms.tasks.OnCompleteListener
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
import java.util.Objects

class ChatActivity : BaseActivity() {
    private lateinit var binding :ActivityChatBinding
    private lateinit var receiverUser :User
    private lateinit var chatMessages :ArrayList<ChatMessage>
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var database:FirebaseFirestore
    private var conversionId:String? = null
    private var isReceiverAvailable =false

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
            Log.e("EventListener", "Error: ${error.message}", error)
            return@EventListener
        }

        if (value != null) {
            val initialCount = chatMessages.size
            for (documentChange in value.documentChanges) {
                if (documentChange.type == DocumentChange.Type.ADDED) {
                    val chatMessage = ChatMessage()
                    chatMessage.senderId= documentChange.document.getString(Constants.KEY_SENDER_ID)
                    chatMessage.receiverId= documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                    chatMessage.message= documentChange.document.getString(Constants.KEY_MESSAGE)
                    chatMessage.dateTime = getReadableDateTime(documentChange.document.getDate(Constants.KEY_TIMESTAMP) ?: Date())
                    chatMessage.dataObject = documentChange.document.getDate(Constants.KEY_TIMESTAMP) ?: Date()
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
        if (conversionId == null) {
            Log.d("EventListener", "conversionId is null, checking for conversion")
            try {
                checkForConversion()
            } catch (e: Exception) {
                Log.e("EventListener", "Error in checkForConversion: ${e.message}", e)
            }
        }
    }


    private fun sendMessage(){
        val message = HashMap<String,Any>()
        message[Constants.KEY_SENDER_ID] = preferenceManager.getString(Constants.KEY_USER_ID)!!
        message[Constants.KEY_RECEIVER_ID] = receiverUser.id!!
        message[Constants.KEY_MESSAGE] = binding.inputMessage.text.toString()
        message[Constants.KEY_TIMESTAMP] = Date()
        database.collection(Constants.KEy_COLLECTION_CHAT).add(message)
        if (conversionId!=null){
            updateConversion(binding.inputMessage.text.toString())
        }else{
            val conversion = HashMap<String,Any>()
            conversion[Constants.KEY_SENDER_ID] = preferenceManager.getString(Constants.KEY_USER_ID)!!
            conversion[Constants.KEY_SENDER_NAME] = preferenceManager.getString(Constants.KEY_NAME)!!
            conversion[Constants.KEY_SENDER_IMAGE] = preferenceManager.getString(Constants.KEY_IMAGE)!!
            conversion[Constants.KEY_RECEIVER_ID] = receiverUser.id!!
            conversion[Constants.KEY_RECEIVER_NAME] = receiverUser.name!!
            conversion[Constants.KEY_RECEIVER_IMAGE] = receiverUser.image!!
            conversion[Constants.KEY_LAST_MESSAGE] = binding.inputMessage.text.toString()
            conversion[Constants.KEY_TIMESTAMP] = Date()
            addConversion(conversion)
        }
        binding.inputMessage.text=null

    }
    private fun listenAvailabilityOfReceiver(){
        database.collection(Constants.KEY_COLLECTION_USERS).document(receiverUser.id!!).
        addSnapshotListener { value, error ->
            if (error!=null){
                return@addSnapshotListener
            }
            if (value!=null){
                if (value.getLong(Constants.KEY_AVAILABILITY)!=null){
                    val availability = Objects.requireNonNull(
                        value.getLong(Constants.KEY_AVAILABILITY)
                    )!!.toInt()
                    isReceiverAvailable=availability==1
                }
            }
            if (isReceiverAvailable){
                binding.textAvailability.visibility=View.VISIBLE
            }else{
                binding.textAvailability.visibility=View.GONE
            }

        }
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
    private fun addConversion(conversion:HashMap<String,Any>){
        database.collection(Constants.KEY_Collection_CONVERSATIONS)
            .add(conversion)
            .addOnSuccessListener { documentReference ->
                conversionId=documentReference.id
            }
    }
    private fun updateConversion(message:String){
        val documentReference=database.collection(Constants.KEY_Collection_CONVERSATIONS).document(conversionId!!)
        documentReference.update(
            Constants.KEY_LAST_MESSAGE,message,
            Constants.KEY_TIMESTAMP,Date()
        )
    }
    private fun setListeners(){
        binding.imageBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.layoutSend.setOnClickListener { sendMessage() }
    }
    private fun checkForConversion(){
        if (chatMessages.size!=0){
            checkForConversionRemotely(
                preferenceManager.getString(Constants.KEY_USER_ID)!!,
                receiverUser.id!!
            )
            checkForConversionRemotely(
                receiverUser.id!!,
                preferenceManager.getString(Constants.KEY_USER_ID)!!
            )
        }
    }
    private fun checkForConversionRemotely(senderId:String,receiverId:String){
        database.collection(Constants.KEY_Collection_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_SENDER_ID,senderId)
            .whereEqualTo(Constants.KEY_RECEIVER_ID,receiverId)
            .get()
            .addOnCompleteListener(conversionOnCompleteListener)
    }
    private val conversionOnCompleteListener =OnCompleteListener<QuerySnapshot>{task->
        if (task.isSuccessful&&task.result!=null&&task.result.documents.size>0){
            val documentSnapShort = task.result.documents[0]
            conversionId=documentSnapShort.id
        }
    }

    override fun onResume() {
        super.onResume()
        listenAvailabilityOfReceiver()
    }
}
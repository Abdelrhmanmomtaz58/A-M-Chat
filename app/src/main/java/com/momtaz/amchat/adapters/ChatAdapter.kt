package com.momtaz.amchat.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.momtaz.amchat.databinding.ItemContainerReceivedMessageBinding
import com.momtaz.amchat.databinding.ItemContainerSentMessageBinding
import com.momtaz.amchat.models.ChatMessage

class ChatAdapter(private val chatMessage:List<ChatMessage>, private val receiverProfileImage:Bitmap, private val senderId: String?)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        val VIEW_TYPE_SENT = 1
        val VIEW_TYPE_RECEIVED=2
    class SentMessageViewHolder(private val binding: ItemContainerSentMessageBinding) :RecyclerView.ViewHolder(binding.root){
        fun setData(chatMessage: ChatMessage){
            binding.textMessage.text = chatMessage.message
            binding.textDateTime.text = chatMessage.dateTime
        }
    }
    class ReceivedMessageViewHolder(private val binding:ItemContainerReceivedMessageBinding):RecyclerView.ViewHolder(binding.root){
        fun setData(chatMessage: ChatMessage,receiverProfileImage:Bitmap){
            binding.textMessage.text=chatMessage.message
            binding.textDateTime.text=chatMessage.dateTime
            binding.imageProfile.setImageBitmap(receiverProfileImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType==VIEW_TYPE_SENT){
            val itemContainerSentMessageBinding = ItemContainerSentMessageBinding.inflate(
                LayoutInflater.from(parent.context),parent,false)
            return SentMessageViewHolder(itemContainerSentMessageBinding)
        }else{
            val itemContainerReceivedMessageBinding = ItemContainerReceivedMessageBinding.inflate(
                LayoutInflater.from(parent.context),parent,false)
            return ReceivedMessageViewHolder(itemContainerReceivedMessageBinding)
        }
    }

    override fun getItemCount(): Int {
        return chatMessage.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position)==VIEW_TYPE_SENT){
            (holder as SentMessageViewHolder).setData(chatMessage[position])
        }else{
            (holder as ReceivedMessageViewHolder).setData(chatMessage[position],receiverProfileImage)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (chatMessage[position].senderId == senderId){
            return VIEW_TYPE_SENT
        }else{
            return VIEW_TYPE_RECEIVED
        }
    }
}
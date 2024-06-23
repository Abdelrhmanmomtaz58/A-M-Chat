package com.momtaz.amchat.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.momtaz.amchat.databinding.ItemContainerRecentConversionBinding
import com.momtaz.amchat.listenrs.ConversionListener
import com.momtaz.amchat.models.ChatMessage
import com.momtaz.amchat.models.User

class RecentConversationsAdapter(
    private val chatMessages:List<ChatMessage>,
    private val conversionListener: ConversionListener)
    :RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder>() {

    class ConversionViewHolder(private val binding:ItemContainerRecentConversionBinding
    ,private val conversionListener: ConversionListener):RecyclerView.ViewHolder(binding.root){

        fun setData(chatMessage: ChatMessage){
            binding.imageProfile.setImageBitmap(getConversionImage(chatMessage.conversionImage))
            binding.textName.text=chatMessage.conversionName
            binding.textRecentMessage.text=chatMessage.message
            binding.root.setOnClickListener {
                val user = User().apply {
                    id = chatMessage.conversionId
                    name = chatMessage.conversionName
                    image = chatMessage.conversionImage
                }

                conversionListener.onConversionClicked(user)

            }
        }


        private fun getConversionImage(encodedImage: String?):Bitmap{
            val byte=Base64.decode(encodedImage,Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(byte,0,byte.size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversionViewHolder {
        return ConversionViewHolder(
            ItemContainerRecentConversionBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            ,conversionListener
        )
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    override fun onBindViewHolder(holder: ConversionViewHolder, position: Int) {
        holder.setData(chatMessages[position])
    }


}
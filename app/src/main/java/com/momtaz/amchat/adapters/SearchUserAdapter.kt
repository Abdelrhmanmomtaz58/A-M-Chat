package com.momtaz.amchat.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.momtaz.amchat.databinding.ItemUserBinding
import com.momtaz.amchat.listenrs.UserListener
import com.momtaz.amchat.models.User

class SearchUserAdapter(private val userList: List<User>,private val userListener: UserListener): RecyclerView.Adapter<SearchUserAdapter.UserViewHolder>() {
    class UserViewHolder(private val binding :ItemUserBinding,private val userListener: UserListener) : RecyclerView.ViewHolder(binding.root) {
        fun setUserData(user: User){
            binding.textName.text=user.name
            binding.textEmail.text=user.email
            binding.imageProfile.setImageBitmap(getUserImage(user.image))
            binding.root.setOnClickListener { userListener.onUserClicked(user) }
        }
        private fun getUserImage(encodedImage: String?): Bitmap {
            val byte = Base64.decode(encodedImage, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(byte,0,byte.size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemUserBinding=ItemUserBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return UserViewHolder(itemUserBinding,userListener)
    }

    override fun getItemCount() = userList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
      holder.setUserData(userList[position])
    }
}
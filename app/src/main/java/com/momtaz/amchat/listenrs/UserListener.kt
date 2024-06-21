package com.momtaz.amchat.listenrs

import com.momtaz.amchat.models.User

interface UserListener {
    fun onUserClicked(user:User)
}
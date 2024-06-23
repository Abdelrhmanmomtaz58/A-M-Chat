package com.momtaz.amchat.listenrs

import com.momtaz.amchat.models.User

interface ConversionListener {
    fun onConversionClicked(user:User)
}
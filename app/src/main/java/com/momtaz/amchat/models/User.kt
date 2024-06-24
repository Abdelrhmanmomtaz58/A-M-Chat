package com.momtaz.amchat.models

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.toObject
import java.io.Serializable

data class User(
    var name: String? = null,
    var image: String? = null,
    var email: String? = null,
    var token: String? = null,
    var id: String? = null
) : Serializable {
    fun fromDocument(document: DocumentSnapshot): User {
        val user = document.toObject<User>()!!
        user.id = document.id
        return user
    }
}

package com.momtaz.amchat.utilities

object Constants {
    const val KEY_COLLECTION_USERS ="users"
    const val KEY_NAME = "name"
    const val KEY_EMAIL ="email"
    const val KEY_PASSWORD ="password"
    const val KEY_PREFERENCE_NAME = "chatAppPreference"
    const val KEY_IS_SIGNED_IN ="isSignedIn"
    const val KEY_USER_ID="userId"
    const val KEY_IMAGE="image"
    const val KEY_FCM_TOKEN ="fcmToken"
    const val KEY_USER ="user"
    const val KEy_COLLECTION_CHAT ="chat"
    const val KEY_SENDER_ID ="senderId"
    const val KEY_RECEIVER_ID="receiverId"
    const val KEY_MESSAGE="message"
    const val KEY_TIMESTAMP="timestamp"
    const val KEY_Collection_CONVERSATIONS="conversations"
    const val KEY_SENDER_NAME="senderName"
    const val KEY_RECEIVER_NAME="receiverName"
    const val KEY_SENDER_IMAGE="senderImage"
    const val KEY_RECEIVER_IMAGE="receiverImage"
    const val KEY_LAST_MESSAGE="lastMessage"
    const val KEY_AVAILABILITY ="availability"
    const val REMOTE_MSG_AUTHORIZATION ="Authorization"
    const val REMOTE_MSG_TYPE="Content-Type"
    const val REMOTE_MSG_DATA="data"
    const val REMOTE_MSG_REGISTRATION_IDS="registration_ids"

    private var remoteMsgHeaders: HashMap<String, String>? =null
    fun getRemoteMsgHeaders():HashMap<String,String>{
        if (remoteMsgHeaders==null){
            remoteMsgHeaders= HashMap()
            remoteMsgHeaders!![REMOTE_MSG_AUTHORIZATION]="key=MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCPXAXp0YOxZtIVYOQ6PeCuft6Ly1SLFWo3FXNRgS0CuD+liGf0mg9jZWH666/UQBF/Sz7NgWqpLFAdhoeatuOab9IaW8GWMf8HJG9t7ov+WEQ8QChR2SAqTp1UTIcI89k5H1N+9CzWbLA/hPQd4wlIEJIKi4P947uXSlpqXmcX9Q8K9NFrAnMyzCmOWoFcCD023Xm9vu6eo5vSDM2xiCHGQ22Ud6pJnVg2c9eKBsVJYHlJyNwlRXvSeBlOoOSw5irf2peismAYDg+hmjMvH4WTF38aaoWht1GOve6tJrRn+NVjiXjtRi3IyCnZf3uPZhZvjRcilk5sdc1xKMwCTCrzAgMBAAECgf8x59E7C5m6AYF1CFBV/RrAqepaNf9FQG+CYk9DTRuM8ItfTfshhXpGH8XaMa7+IvIJbW55/YVtmYJvrEqOcKnX6EVWuO6jFZuZmscj+uuvlwOZFWvkn8CIQSCWj7JKtAV6aEyb5C7p82gioey9+ib3XBACijYSORolIZ2RYIdJwl9zqsOIhTs8IUXMjDT8xEXzmShLQgnlw43gCLnB72snEzrGkPzNnYMpmFA04fuwH+j4iFlYcwg9cnJ6rVYRpz7sdt8CrV4sk7v7JZivhbs/KnjarKDRdqKC3/iCavkAFGTKt8WpPfxvPxQK4fjv8+8Kc8UaeMlC5tmY9GZFvrkCgYEAwG4K2c9FREm8xuLo5IeVM+Emu5+9iyrO5kP2De62w8i69W7m+ISw6hdZow1CeqlP1axQn2VvbO17ihB28wOgLPZcY3IenvEvQ8iu5PVE7wA9zhx1V6x61M3obKkqYDWizR4YR14vKJkuDQpM4RJNAphvxbOA7zyyQh2Zw34mt88CgYEAvrgOUvEtqqYIFy1D2BbRMAncgfVbZdNxNNPpXupAHh8jAHcU8vXjZ4uwgev1XgMP+Hf7ky9RVVT3C0AuVKNIRmq2a+WdGHbexU/qMpkRdt1YyY3wjdEqEY6gwhNeyfONngpRtOWyyRG5ISx3CxNKLTnqjIQY5xGm+I75iXh2v50CgYEAsAN39Wc76oywGhPwBylldoDZ1wD0dVmAyusbI/9YOZlgOaNpraRh1p3pJdW5G03HcqEdVc+IrlBDkjQhOYP1fWsTcvstuyDNpOjp4Q/gL+U+o0swojNoX0LA+LN7yWkGMyz19EHYTQBgORlU3QLKYjdg9auNokcYnpVI0qKPc80CgYAH4inNaeS+WhCZjKhyRTcuainwQUke42eZjGzoHHy8AgN7YHDAPkPBIvgP1sYUtcGyPptqkRkmktWHZIbFvt4c8wpJWXtt6HP66fu/Ta54IFwsMK7R3eSER0YD37MaW/PtpXWNKbWDNDVX1rOxBIIbbnpwhoFVA9jIC3jrIsydzQKBgQCEY1u11djrxcafQh2tZtJJxFVgTyzc6cktGra/6MyvEPrpYyNHPs/obSluZuQ7rvngm7TJh6jpzSw+8AsNxiwiY+Ky336l+eIxQVDfSzULQkknz2ziIfq8w0jeBvpoWrsc0q0CZHm5VNfP5ujk7kD1DZTKDzH/ud9r5X/Bzdvcrg=="
        }
        remoteMsgHeaders!![REMOTE_MSG_TYPE] = "application/json"
        return remoteMsgHeaders!!
    }

}
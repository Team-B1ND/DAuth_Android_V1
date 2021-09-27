package kr.hs.dgsw.smartschool.dauth.api.model.request

import com.google.gson.annotations.SerializedName

data class TokenRequest(
    val code: String,
    @SerializedName("client_id")
    val clientId: String,
    @SerializedName("client_secret")
    val clientSecret: String
)

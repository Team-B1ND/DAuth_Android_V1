package kr.hs.dgsw.smartschool.dauth_android_v1.api.model.request

data class TokenRequest(
    val code: String,
    val clientId: String,
    val clientSecret: String
)

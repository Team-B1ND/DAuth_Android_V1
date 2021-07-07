package kr.hs.dgsw.smartschool.dauth_android_v1.api.model.request

data class LoginRequest(
    val id: String,
    val pw: String,
    val clientId: String,
    val redirectUrl: String
)
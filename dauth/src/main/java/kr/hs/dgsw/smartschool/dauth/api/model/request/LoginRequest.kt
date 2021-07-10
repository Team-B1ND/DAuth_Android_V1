package kr.hs.dgsw.smartschool.dauth.api.model.request

data class LoginRequest(
    val id: String,
    val pw: String,
    val clientId: String,
    val redirectUrl: String
)
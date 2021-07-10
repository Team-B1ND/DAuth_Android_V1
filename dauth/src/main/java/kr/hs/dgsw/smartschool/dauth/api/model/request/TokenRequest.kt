package kr.hs.dgsw.smartschool.dauth.api.model.request

data class TokenRequest(
    val code: String,
    val clientId: String,
    val clientSecret: String
)

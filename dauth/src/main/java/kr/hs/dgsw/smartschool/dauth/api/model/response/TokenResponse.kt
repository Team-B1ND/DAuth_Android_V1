package kr.hs.dgsw.smartschool.dauth.api.model.response

data class TokenResponse(
    val token: String,
    val refreshToken: String
)

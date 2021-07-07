package kr.hs.dgsw.smartschool.dauth_android_v1.api.model.response

data class TokenResponse(
    val token: String,
    val refreshToken: String
)

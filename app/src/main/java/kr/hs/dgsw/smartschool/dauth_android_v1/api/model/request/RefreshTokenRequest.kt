package kr.hs.dgsw.smartschool.dauth_android_v1.api.model.request

data class RefreshTokenRequest(
    val refreshToken: String,
    val clientId: String
)

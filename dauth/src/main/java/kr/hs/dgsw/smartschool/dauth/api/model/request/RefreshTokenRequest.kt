package kr.hs.dgsw.smartschool.dauth.api.model.request

data class RefreshTokenRequest(
    val refreshToken: String,
    val clientId: String
)

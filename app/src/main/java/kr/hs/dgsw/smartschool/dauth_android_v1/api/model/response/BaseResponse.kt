package kr.hs.dgsw.smartschool.dauth_android_v1.api.model.response

data class BaseResponse<T>(
    val status: Int,
    val message: String,
    val data: T
)
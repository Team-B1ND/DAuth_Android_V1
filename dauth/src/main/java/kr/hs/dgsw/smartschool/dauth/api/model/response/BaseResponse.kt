package kr.hs.dgsw.smartschool.dauth.api.model.response

data class BaseResponse<T>(
    val status: Int,
    val message: String,
    val data: T
)
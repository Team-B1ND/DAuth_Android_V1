package kr.hs.dgsw.smartschool.dauth.api.model.response

data class UserInfoResponse(
    val accessLevel: Int,
    val grade: Int,
    val name: String,
    val number: Int,
    val profileImage: String,
    val email: String,
    val room: Int,
    val uniqueId: String,
)
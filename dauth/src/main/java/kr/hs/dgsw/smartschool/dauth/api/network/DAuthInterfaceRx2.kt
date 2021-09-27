package kr.hs.dgsw.smartschool.dauth.api.network

import io.reactivex.Single
import kr.hs.dgsw.smartschool.dauth.api.model.request.LoginRequest
import kr.hs.dgsw.smartschool.dauth.api.model.request.RefreshTokenRequest
import kr.hs.dgsw.smartschool.dauth.api.model.request.TokenRequest
import kr.hs.dgsw.smartschool.dauth.api.model.response.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface DAuthInterfaceRx2 {
    @POST("auth/login")
    fun login(@Body loginRequest: LoginRequest): Single<Response<BaseResponse<LoginResponse>>>

    @POST("token")
    fun getToken(@Body tokenRequest: TokenRequest): Single<Response<TokenResponse>>

    @POST("token/refresh")
    fun getRefreshToken(@Body refreshTokenRequest: RefreshTokenRequest): Single<Response<BaseResponse<RefreshTokenResponse>>>

    @GET("user")
    fun getUserInfo(@Header("access-token") token: String): Single<Response<BaseResponse<UserInfoResponse>>>

}
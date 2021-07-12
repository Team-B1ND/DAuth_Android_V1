package kr.hs.dgsw.smartschool.dauth.api.network

import io.reactivex.Single
import kr.hs.dgsw.smartschool.dauth.api.model.request.LoginRequest
import kr.hs.dgsw.smartschool.dauth.api.model.request.RefreshTokenRequest
import kr.hs.dgsw.smartschool.dauth.api.model.request.TokenRequest
import kr.hs.dgsw.smartschool.dauth.api.model.response.BaseResponse
import kr.hs.dgsw.smartschool.dauth.api.model.response.LoginResponse
import kr.hs.dgsw.smartschool.dauth.api.model.response.RefreshTokenResponse
import kr.hs.dgsw.smartschool.dauth.api.model.response.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface DAuthInterface {
    @POST("auth/login")
    fun login(@Body loginRequest: LoginRequest): Single<Response<BaseResponse<LoginResponse>>>

    @POST("token")
    fun getToken(@Body tokenRequest: TokenRequest): Single<Response<BaseResponse<TokenResponse>>>

    @POST("token/refresh")
    fun getRefreshToken(@Body refreshTokenRequest: RefreshTokenRequest): Single<Response<BaseResponse<RefreshTokenResponse>>>

}
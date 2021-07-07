package kr.hs.dgsw.smartschool.dauth_android_v1.api.network

import com.google.gson.GsonBuilder
import io.reactivex.Single
import kr.hs.dgsw.smartschool.dauth_android_v1.api.model.request.LoginRequest
import kr.hs.dgsw.smartschool.dauth_android_v1.api.model.request.RefreshTokenRequest
import kr.hs.dgsw.smartschool.dauth_android_v1.api.model.request.TokenRequest
import kr.hs.dgsw.smartschool.dauth_android_v1.api.model.response.BaseResponse
import kr.hs.dgsw.smartschool.dauth_android_v1.api.model.response.LoginResponse
import kr.hs.dgsw.smartschool.dauth_android_v1.api.model.response.RefreshTokenResponse
import kr.hs.dgsw.smartschool.dauth_android_v1.api.model.response.TokenResponse
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.Executors

object DAuthServer {

    private const val url = "http://dauth.b1nd.com/api/"

    private val dAuth = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .callbackExecutor(Executors.newSingleThreadExecutor())
        .build()
        .create(DAuthInterface::class.java)

    fun login(loginRequest: LoginRequest): Single<BaseResponse<LoginResponse>> =
        dAuth.login(loginRequest)

    fun getToken(tokenRequest: TokenRequest): Single<BaseResponse<TokenResponse>> =
        dAuth.getToken(tokenRequest)

    fun getRefreshToken(refreshTokenRequest: RefreshTokenRequest): Single<BaseResponse<RefreshTokenResponse>> =
        dAuth.getRefreshToken(refreshTokenRequest)


}
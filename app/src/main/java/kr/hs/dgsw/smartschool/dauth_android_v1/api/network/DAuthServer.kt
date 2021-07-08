package kr.hs.dgsw.smartschool.dauth_android_v1.api.network

import android.content.ComponentName
import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private fun login(loginRequest: LoginRequest): Single<BaseResponse<LoginResponse>> =
        dAuth.login(loginRequest)

    private fun getToken(tokenRequest: TokenRequest): Single<BaseResponse<TokenResponse>> =
        dAuth.getToken(tokenRequest)

    private fun getRefreshToken(refreshTokenRequest: RefreshTokenRequest): Single<BaseResponse<RefreshTokenResponse>> =
        dAuth.getRefreshToken(refreshTokenRequest)

    private val dodamResult = MutableLiveData<Single<TokenResponse>>()

    fun loginForDodam(
        register: ActivityResultLauncher<Intent>,
    ): LiveData<Single<TokenResponse>> {
        val component = ComponentName("kr.hs.dgsw.smartschool.dodamdodam",
            "kr.hs.dgsw.smartschool.dodamdodam.view.activity.ProvideAccountForDAuthActivity")
        val intent = Intent(Intent.ACTION_MAIN)
        intent.component = component

        register.launch(intent)

        return dodamResult
    }

    fun ComponentActivity.settingForDodam(
        clientId: String,
        clientSecret: String,
        redirectUrl: String,
    ): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == 200) {
                val id = activityResult.data?.getStringExtra("id") ?: ""
                val pw = activityResult.data?.getStringExtra("pw") ?: ""
                val loginRequest = LoginRequest(id, pw, clientId, redirectUrl)
                dodamResult.value = login(loginRequest)
                    .map { it.data.location.split("?code=")[1] }
                    .flatMap { getToken(TokenRequest(it, clientId, clientSecret)) }
                    .map { it.data }
            } else {
                Log.d("DAUTH_TEST_TAG_!@#ASD", "FAIULRE")
            }
        }
    }


}

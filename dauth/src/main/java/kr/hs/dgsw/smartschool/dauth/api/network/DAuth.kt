package kr.hs.dgsw.smartschool.dauth.api.network

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import io.reactivex.Single
import kr.hs.dgsw.smartschool.dauth.R
import kr.hs.dgsw.smartschool.dauth.api.App.Companion.context
import kr.hs.dgsw.smartschool.dauth.api.model.request.LoginRequest
import kr.hs.dgsw.smartschool.dauth.api.model.request.RefreshTokenRequest
import kr.hs.dgsw.smartschool.dauth.api.model.request.TokenRequest
import kr.hs.dgsw.smartschool.dauth.api.model.response.BaseResponse
import kr.hs.dgsw.smartschool.dauth.api.model.response.LoginResponse
import kr.hs.dgsw.smartschool.dauth.api.model.response.RefreshTokenResponse
import kr.hs.dgsw.smartschool.dauth.api.model.response.TokenResponse
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.concurrent.Executors

object DAuth {
    private const val dodamPackage = "kr.hs.dgsw.smartschool.dodamdodam"
    private val dAuth = Retrofit.Builder()
        .baseUrl(context().getString(R.string.url))
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .callbackExecutor(Executors.newSingleThreadExecutor())
        .build()
        .create(DAuthInterface::class.java)

    private fun <T> checkError(response: retrofit2.Response<BaseResponse<T>>): BaseResponse<T> {
        if (!response.isSuccessful) {
            val errorBody = JSONObject(response.errorBody()!!.string())
            throw Throwable(errorBody.getString("message"))
        }
        return response.body()!!
    }

    private fun login(loginRequest: LoginRequest): Single<BaseResponse<LoginResponse>> =
        dAuth.login(loginRequest).map(this::checkError)

    private fun getToken(tokenRequest: TokenRequest): Single<BaseResponse<TokenResponse>> =
        dAuth.getToken(tokenRequest).map(this::checkError)

    fun getRefreshToken(refreshTokenRequest: RefreshTokenRequest): Single<BaseResponse<RefreshTokenResponse>> =
        dAuth.getRefreshToken(refreshTokenRequest).map(this::checkError)

    private val dodamResult = MutableLiveData<Single<TokenResponse>>()

    private val installed = MutableLiveData(true)

    fun Context.loginForDodam(
        register: ActivityResultLauncher<Intent>,
    ): LiveData<Single<TokenResponse>> {
        val component = ComponentName(dodamPackage,
            "kr.hs.dgsw.smartschool.dodamdodam.view.activity.DAuthActivity")
        val intent = Intent(Intent.ACTION_MAIN)
        intent.component = component


        if (installed.value == true) register.launch(intent)
        else {
            dodamResult.value = Single.error(Exception("도담도담을 설치해주세요"))
            try {
                startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$dodamPackage")))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$dodamPackage")))
            }
        }
        return dodamResult
    }

    fun ComponentActivity.settingForDodam(
        clientId: String,
        clientSecret: String,
        redirectUrl: String,
    ): ActivityResultLauncher<Intent> {
        val intent = packageManager.getLaunchIntentForPackage("kr.hs.dgsw.smartschool.dodamdodam")

        installed.value = intent != null

        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == 200) {
                val id = activityResult.data?.getStringExtra("id") ?: ""
                val pw = activityResult.data?.getStringExtra("pw") ?: ""
                val loginRequest = LoginRequest(id, pw, clientId, redirectUrl)
                dodamResult.value = login(loginRequest)
                    .map { it.data.location.split("?code=")[1] }
                    .flatMap { getToken(TokenRequest(it, clientId, clientSecret)) }
                    .map { it.data }
            }
        }
    }


}

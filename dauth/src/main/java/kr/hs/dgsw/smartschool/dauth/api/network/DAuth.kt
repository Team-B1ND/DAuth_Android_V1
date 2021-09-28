package kr.hs.dgsw.smartschool.dauth.api.network

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kr.hs.dgsw.smartschool.dauth.R
import kr.hs.dgsw.smartschool.dauth.api.App.Companion.context
import kr.hs.dgsw.smartschool.dauth.api.model.request.LoginRequest
import kr.hs.dgsw.smartschool.dauth.api.model.request.RefreshTokenRequest
import kr.hs.dgsw.smartschool.dauth.api.model.request.TokenRequest
import kr.hs.dgsw.smartschool.dauth.api.model.response.BaseResponse
import kr.hs.dgsw.smartschool.dauth.api.model.response.TokenResponse
import kr.hs.dgsw.smartschool.dauth.api.model.response.*
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors

object DAuth {
    private val dodamPackage = context().getString(R.string.dodamPackage)

    private val retrofit = Retrofit.Builder()
        .baseUrl(context().getString(R.string.url))
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .callbackExecutor(Executors.newSingleThreadExecutor())
        .build()

    private val dAuth = retrofit
        .create(DAuthInterfaceRx2::class.java)

    private val openApi = Retrofit.Builder()
        .baseUrl("http://open.dodam.b1nd.com/api/")
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .callbackExecutor(Executors.newSingleThreadExecutor())
        .build()
        .create(DAuthInterfaceRx2::class.java)

    private var dodamResult = PublishSubject.create<TokenResponse>()

    private val installed = MutableLiveData(true)

    private val compositeDisposable = CompositeDisposable()

    private fun <T> checkError(response: retrofit2.Response<BaseResponse<T>>): BaseResponse<T> {
        if (!response.isSuccessful) {
            val errorBody = JSONObject(response.errorBody()!!.string())
            throw Throwable(errorBody.getString("message"))
        }
        return response.body()!!
    }

    private fun login(loginRequest: LoginRequest): Single<BaseResponse<LoginResponse>> =
        dAuth.login(loginRequest).map(this::checkError)

    private fun getToken(tokenRequest: TokenRequest): Single<TokenResponse> =
        dAuth.getToken(tokenRequest).map {
            if (it.isSuccessful) {
                return@map TokenResponse(it.body()?.token ?: "", it.body()?.refreshToken ?: "")
            } else {
                it.errorBody()?.let { body ->
                    val response =
                        retrofit.responseBodyConverter<BaseResponse<Unit>>(BaseResponse::class.java,
                            BaseResponse::class.java.annotations).convert(body)
                    throw Throwable(response?.message)
                } ?: throw Throwable(it.message())
            }
        }

    fun getRefreshToken(refreshTokenRequest: RefreshTokenRequest): Single<BaseResponse<RefreshTokenResponse>> =
        dAuth.getRefreshToken(refreshTokenRequest).map(this::checkError)

    fun getUserInfo(token: String): Single<BaseResponse<UserInfoResponse>> =
        openApi.getUserInfo(token).map(this::checkError)

    fun Context.loginForDodam(
        register: ActivityResultLauncher<Intent>,
        onSuccess: (TokenResponse) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        val component = ComponentName(dodamPackage,
            context().getString(R.string.activityUrl))
        val intent = Intent(Intent.ACTION_MAIN)
        intent.component = component

        if (installed.value == true) register.launch(intent)
        else {
            dodamResult.onError(Throwable("도담도담을 설치해주세요"))
            try {
                startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$dodamPackage")))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$dodamPackage")))
            }
        }

        compositeDisposable.add(dodamResult
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    onSuccess(it)
                    dodamResult = PublishSubject.create()
                }, {
                    onFailure(it)
                    dodamResult = PublishSubject.create()
                }))
    }

    fun ComponentActivity.settingForDodam(
        clientId: String,
        clientSecret: String,
        redirectUrl: String,
    ): ActivityResultLauncher<Intent> {
        val intent = packageManager.getLaunchIntentForPackage(dodamPackage)

        installed.value = intent != null

        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == 200) {
                val id = activityResult.data?.getStringExtra("id") ?: ""
                val pw = activityResult.data?.getStringExtra("pw") ?: ""
                val loginRequest = LoginRequest(id, pw, clientId, redirectUrl)

                compositeDisposable.add(
                    login(loginRequest)
                        .map {
                            Uri.parse(it.data.location).getQueryParameter("code")
                        }
                        .flatMap { getToken(TokenRequest(it, clientId, clientSecret)) }
                        .map { it }.observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io()).subscribe({
                            dodamResult.onNext(it)
                        }, {
                            dodamResult.onError(it)
                        })
                )
            }
        }
    }

    fun dispose() {
        compositeDisposable.clear()
    }

}

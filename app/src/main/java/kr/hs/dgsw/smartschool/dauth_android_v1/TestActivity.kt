package kr.hs.dgsw.smartschool.dauth_android_v1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kr.hs.dgsw.smartschool.dauth_android_v1.api.model.request.LoginRequest
import kr.hs.dgsw.smartschool.dauth_android_v1.api.model.request.TokenRequest
import kr.hs.dgsw.smartschool.dauth_android_v1.api.network.DAuthServer

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val clientId = "266f1b55522f4b7fb3a08e2ca0bec3b77ac08096f500447d8c46ad14e650f13d"
        val redirectUrl = "http://naver.com/redirect"
        val clientSecret = "1a975dead9b74e9aa7beb200a8bbe6b72186db808f094d9c81b966a7d90fcc64"
        val id = "hsasy0113"
        val pw =
            "0C81E8E706BD85DDE06693DA6F23976F791B452012D58628E1E7E10C25D66D0FB10820D418793FCC03C9308F6F1F26F2A4DE4076F0A856F5319B74E7C2F1A61B"

        val compositeDisposable = CompositeDisposable()

        val loginRequest = LoginRequest(id, pw, clientId, redirectUrl)

//        compositeDisposable.add(
//            DAuthServer.login(loginRequest)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .doOnSuccess {
//
//                }.subscribe({
//
//                }, {
//
//                })
//        )
        compositeDisposable.add(
            DAuthServer.login(loginRequest)
                .map { it.data.location.split("?code=")[1] }
                .flatMap { DAuthServer.getToken(TokenRequest(it, clientId, clientSecret)) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Toast.makeText(this, "${it.message} ${it.data.token}", Toast.LENGTH_SHORT).show()
                }, {
                    Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
                })
        )

    }
}
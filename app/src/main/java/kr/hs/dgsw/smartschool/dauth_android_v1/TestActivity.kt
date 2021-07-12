package kr.hs.dgsw.smartschool.dauth_android_v1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kr.hs.dgsw.smartschool.dauth.api.network.DAuth.loginForDodam
import kr.hs.dgsw.smartschool.dauth.api.network.DAuth.settingForDodam

class TestActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val clientId = "266f1b55522f4b7fb3a08e2ca0bec3b77ac08096f500447d8c46ad14e650f13d"
        val redirectUrl = "http://naver.com/redirect"
        val clientSecret = "1a975dead9b74e9aa7beb200a8bbe6b72186db808f094d9c81b966a7d90fcc64"
        val compositeDisposable = CompositeDisposable()
        val register = settingForDodam(clientId, clientSecret, redirectUrl)

        findViewById<Button>(R.id.btn).setOnClickListener {
            loginForDodam(register).observe(this, { single ->
                compositeDisposable.add(
                    single
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            Toast.makeText(this, it.token, Toast.LENGTH_SHORT).show()
                        }, {
                            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                        })
                )
            })
        }

    }
}
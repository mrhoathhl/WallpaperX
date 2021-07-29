package com.leoo.hdwallpaperx

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth

class WelcomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val SPLASH_TIME_OUT = 1600L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        MobileAds.initialize(this) {}
        val mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = this?.resources?.getString(R.string.admod_id_interstitial_1)
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        auth = FirebaseAuth.getInstance()
        signIn("mrhoathhl@gmail.com", "123456789")

        Handler().postDelayed({
            mInterstitialAd.adListener = object : AdListener() {
                override fun onAdClosed() {
                    val i = Intent(this@WelcomeActivity, MainActivity::class.java)
                    startActivity(i)
                    finish()
                }
            }
            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
            } else {
                val i = Intent(this@WelcomeActivity, MainActivity::class.java)
                startActivity(i)
                finish()
            }

            }, SPLASH_TIME_OUT)
    }

    private fun signIn(email: String, password: String) {
        this.let {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        println("Sign In Successfully")
                        Log.d(auth.toString(), "Sign In Successfully")
                    }
                }
        }
    }
}

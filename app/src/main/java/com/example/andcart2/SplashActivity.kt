package com.example.andcart2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.andcart2.R
import com.example.andcart2.ScrollingActivity
import java.time.Clock.system

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        val anim = AnimationUtils.loadAnimation(this, R.anim.animation)
        val intent = Intent(this, ScrollingActivity::class.java)
        val animListener = object: Animation.AnimationListener{

            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                Handler().postDelayed({
                    startActivity(intent)
                    finish()
                }, 3000) // 3000 is the delayed time in milliseconds.
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        }

        val img = findViewById<ImageView>(R.id.splash)
        anim.setAnimationListener(animListener)
        img.startAnimation(anim)

    }
}

package hse24

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.hse24.challenge.R
import hse24.shop.ShoppingActivity

class MainActivity : AppCompatActivity() {

    private val logoView by lazy {
        findViewById<ImageView>(R.id.main_activity_logo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AnimationUtils.loadAnimation(applicationContext, R.anim.splash_animation).apply {
            logoView.startAnimation(this)
            this.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(p0: Animation?) {
                    //no-op
                }

                override fun onAnimationEnd(p0: Animation?) {
                    startActivity(ShoppingActivity.createIntent(applicationContext))
                    finish()
                }

                override fun onAnimationStart(p0: Animation?) {
                    //no-op
                }
            })
        }
    }
}

package gupta.saurabh.sms.ui.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import gupta.saurabh.sms.R
import gupta.saurabh.sms.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DURATION = 3000L

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivitySplashBinding.inflate(layoutInflater)

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)

            insets
        }

        Handler(Looper.getMainLooper()).postDelayed({

            MainActivity.launchActivity(this@SplashActivity)

        }, SPLASH_DURATION)
    }
}
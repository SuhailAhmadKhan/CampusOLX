package com.example.campusolx.activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.campusolx.R
import com.example.campusolx.data.AuthDatabase
import com.example.campusolx.data.AuthTokenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Date

class SplashActivity : AppCompatActivity() {

    private lateinit var authTokenRepository: AuthTokenRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        authTokenRepository = AuthTokenRepository(AuthDatabase.getDatabase(this).authTokenDao())

        lifecycleScope.launch {
            val authToken = withContext(Dispatchers.IO) {
                authTokenRepository.getAuthToken()
            }
            val currentTime = Date(System.currentTimeMillis())

            val resultIntent = if (authToken != null && currentTime.before(authToken.expirationTime)) {
                // Access token is not null and valid, redirect to home activity
                Intent(this@SplashActivity, MainActivity::class.java)
            } else {
                // Access token is null or expired, redirect to login activity and delete entry
                if (authToken != null) {
                    // Delete the entry in the AuthTokenRepository database
                    authTokenRepository.deleteAuthToken(authToken)
                }
                // Access token is null or expired, redirect to login activity
                Intent(this@SplashActivity, MainActivity2::class.java)
            }

            // Delayed action using a Handler to navigate to the LoginActivity after 3 seconds
            Handler().postDelayed({
                startActivity(resultIntent)
                finish() // Finish the current SplashActivity after navigating
            }, 3000) // Delay for 3000 milliseconds (3 seconds) before starting LoginActivity
        }
    }
}
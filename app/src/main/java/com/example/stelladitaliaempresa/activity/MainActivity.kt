package com.example.stelladitaliaempresa.activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.apkstelladitalia20.activity.AuthenticationActivity

import com.example.stelladitaliaempresa.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var  binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


        Handler(Looper.getMainLooper()).postDelayed({
            abrirAutenticacao()
        }, 3000)


    }


    private fun abrirAutenticacao() {
        val i = Intent(this, AuthenticationActivity::class.java)
        startActivity(i)
        finish()
    }
}

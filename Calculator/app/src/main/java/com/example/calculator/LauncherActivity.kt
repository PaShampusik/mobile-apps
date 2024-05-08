package com.example.calculator

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(Intent(this, PassKeySetupActivity::class.java))

        // Finish the launcher activity so that it's not shown when Back button is pressed
        finish()
    }
}
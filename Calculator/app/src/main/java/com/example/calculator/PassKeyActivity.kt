package com.example.calculator
import android.content.Context
import android.content.Intent
import androidx.biometric.BiometricPrompt;
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator

class PassKeySetupActivity : AppCompatActivity() {

    private lateinit var passKeyEditText: EditText
    private lateinit var confirmPassKeyEditText: EditText
    private lateinit var setupButton: Button
    private lateinit var resetPassKeyCheckBox: CheckBox
    private lateinit var skipButton: Button
    private lateinit var fingerprintButton: Button

    companion object {
        const val ALIAS = "pass_key_alias"
        const val PREF_FILE_NAME = "pass_key_prefs"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_key)

        passKeyEditText = findViewById(R.id.passKeyEditText)
        confirmPassKeyEditText = findViewById(R.id.confirmPassKeyEditText)
        setupButton = findViewById(R.id.setupButton)
        resetPassKeyCheckBox = findViewById(R.id.resetPassKeyCheckBox)
        skipButton = findViewById(R.id.skipButton)
        fingerprintButton = findViewById(R.id.finger)

        val biometricPrompt = androidx.biometric.BiometricPrompt(
            this,
            ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(this@PassKeySetupActivity, "Authentication error", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(this@PassKeySetupActivity, "Authentication success", Toast.LENGTH_SHORT).show()
                    startMainActivity(true)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(this@PassKeySetupActivity, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authenticate with fingerprint")
            .setSubtitle("Place your finger on the sensor")
            .setDescription("Confirm your identity to access the app")
            .setNegativeButtonText("Cancel")
            .build()

        setupButton.setOnClickListener {
            val passKey = passKeyEditText.text.toString()
            val confirmPassKey = confirmPassKeyEditText.text.toString()

            if (passKey.isNotEmpty() && passKey == confirmPassKey) {
                if (resetPassKeyCheckBox.isChecked) {
                    deletePassKey()
                    savePassKey(passKey)
                    Toast.makeText(this, "Pass Key reseted.", Toast.LENGTH_SHORT).show()
                    finish()
                    startMainActivity(true)
                }else if(passKey == getPassKey()){
                    savePassKey(passKey)
                    Toast.makeText(this, "Pass Key is valid!", Toast.LENGTH_SHORT).show()
                    finish()
                    startMainActivity(true)
                }else {
                    if (getPassKey() == null) {
                        savePassKey(passKey)
                        Toast.makeText(this, "Pass Key is set up!", Toast.LENGTH_SHORT).show()
                        finish()
                        startMainActivity(true)
                    } else {
                        Toast.makeText(this, "Pass Key is wrong!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Pass Keys do not match.", Toast.LENGTH_SHORT).show()
            }
        }

        skipButton.setOnClickListener {
            // Start MainActivity without pass key check
            startMainActivity(false)
        }

        fingerprintButton.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }

        biometricPrompt.authenticate(promptInfo)
    }

    private fun savePassKey(passKey: String) {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPreferences = EncryptedSharedPreferences.create(
            PREF_FILE_NAME,
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val editor = sharedPreferences.edit()
        editor.putString("pass_key", passKey)
        editor.apply()
    }

    private fun startMainActivity(flag: Boolean)
    {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("PASS_KEY", flag)
        startActivity(intent)
        finish()
    }


    private fun getPassKey(): String? {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPreferences = EncryptedSharedPreferences.create(
            PREF_FILE_NAME,
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        return sharedPreferences.getString("pass_key", null)
    }

    private fun deletePassKey() {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPreferences = EncryptedSharedPreferences.create(
            PREF_FILE_NAME,
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val editor = sharedPreferences.edit()
        editor.remove("pass_key")
        editor.apply()
        //Toast.makeText(this, "Pass Key deleted.", Toast.LENGTH_SHORT).show()
    }
}
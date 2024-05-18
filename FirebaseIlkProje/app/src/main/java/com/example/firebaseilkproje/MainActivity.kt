package com.example.firebaseilkproje

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.firebaseilkproje.databinding.ActivityMainBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private var storage = Firebase.storage
    private lateinit var firebaseAnalytics: FirebaseAnalytics



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpButton.setOnClickListener { signUp() }
        binding.signInButton.setOnClickListener { signIn() }

        // Initialize Firebase Auth
        auth = Firebase.auth

        storage = FirebaseStorage.getInstance()
        firebaseAnalytics = Firebase.analytics


        // uygulamaya cokturmek için
        // Crashlytics i test etmek için
        //throw RuntimeException("Test Crash")

        // eger kullanıcı daha önce giriş yaptıysa, uygulamaya girişte MainActivity ekranı gelmeden ShareActivity ekranına gececek
        val currentUser = auth.currentUser
        if (currentUser != null){
            visibility()
        }
    }
    @SuppressLint("InvalidAnalyticsName")
    private fun signUp(){
        var email = binding.emailEditText.text.toString()
        var password = binding.passwordEditText.text.toString()
        var username = binding.userNameEditText.text.toString()

        val dataBundle = Bundle()
        dataBundle.putString("kullanici","fatma")
        firebaseAnalytics.logEvent("fav_eklendi",dataBundle)

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful){
                // kullanıcı adı güncelle
                val currentUser = auth.currentUser
                val profileUpdates = userProfileChangeRequest {
                    displayName = username
                }
                currentUser!!.updateProfile(profileUpdates).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(applicationContext, "username added ", Toast.LENGTH_LONG).show()
                            Log.d("TAG", "User profile updated.")
                        }
                    }
                Log.d("TAG", "createUserWithEmail:success")
                visibility()
            }
        }.addOnFailureListener {
            Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }
    private fun signIn(){
        var email = binding.emailEditText.text.toString()
        var password = binding.passwordEditText.text.toString()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful){
                Log.d("TAG", "signInWithEmail:success")
                var currentUser = auth.currentUser?.displayName.toString()
                Toast.makeText(applicationContext, "Welcome: $currentUser ", Toast.LENGTH_LONG).show()
                visibility()
            }
        }.addOnFailureListener {
            Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }
    private fun visibility() {
        val intent = Intent(this, ShareActivity::class.java)
        startActivity(intent)
    }
}
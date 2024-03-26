package com.example.imageshareappwithfirebase.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.imageshareappwithfirebase.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // we have to initialize
        auth = FirebaseAuth.getInstance()

        // if user is already logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // user already log in before
            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

    fun girisYap(view: View){

        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val currentUser = auth.currentUser?.email.toString()
                Toast.makeText(this, "Hoşgeldin: ${currentUser}", Toast.LENGTH_LONG).show()

                val intent = Intent(this, FeedActivity::class.java)
                startActivity(intent)
                finish()

            }
        }.addOnFailureListener { excaption ->
            Toast.makeText(this, excaption.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    fun kayitOl(view: View){

        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->

            // asekron
            if (task.isSuccessful) {

                // going to other activity
                val intent = Intent(this, FeedActivity::class.java)
                startActivity(intent)
                finish()

            }
        }.addOnFailureListener { excaption ->

            Toast.makeText(applicationContext, excaption.localizedMessage, Toast.LENGTH_LONG).show()

        }
    }
}
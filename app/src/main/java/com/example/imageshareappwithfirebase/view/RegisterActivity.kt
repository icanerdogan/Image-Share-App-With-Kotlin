package com.example.imageshareappwithfirebase.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.imageshareappwithfirebase.R
import com.example.imageshareappwithfirebase.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // load cities to spinner
        setupCitySpinner()

        // Set up click listener for logInTV
        binding.logInTV.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.signUp.setOnClickListener {
            registerUser()
        }
    }

    private fun setupCitySpinner() {
        val cities = resources.getStringArray(R.array.turkey_cities)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCity.adapter = adapter
    }

    private fun registerUser() {
        val name = binding.nameET.text.toString()
        val email = binding.emailET.text.toString()
        val city = binding.spinnerCity.selectedItem.toString()
        val password = binding.passwordEt.text.toString()
        val auth = FirebaseAuth.getInstance()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    saveUserToFirestore(currentUser.uid, name, email, city)
                }
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun saveUserToFirestore(uid: String, name: String, email: String, city: String) {
        val db = FirebaseFirestore.getInstance()
        val user = hashMapOf(
            "uid" to uid,
            "name" to name,
            "email" to email,
            "city" to city
        )

        db.collection("Users").document(uid)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Kullanıcı bilgileri kaydedildi.", Toast.LENGTH_LONG).show()
                val intent = Intent(this, FeedActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Kullanıcı bilgileri kaydedilemedi: ${exception.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}

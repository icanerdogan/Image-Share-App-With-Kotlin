package com.example.imageshareappwithfirebase

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.imageshareappwithfirebase.databinding.ActivityImageShareBinding
import com.example.imageshareappwithfirebase.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.net.URI
import java.sql.Timestamp
import java.util.UUID

class ImageShareActivity : AppCompatActivity() {

    var selectedImage : Uri? = null
    var selectedBitMap : Bitmap? = null
    private lateinit var binding : ActivityImageShareBinding
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageShareBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
    }


    fun share(view: View) {

        val referance = storage.reference

        // we should use UUID
        val uuid = UUID.randomUUID()
        val imageName = "${uuid}.jpg"

        val imageReference = referance.child("images").child(imageName)

        if (selectedImage != null) {
            imageReference.putFile(selectedImage!!).addOnSuccessListener {
                val uploadedImageReference = FirebaseStorage.getInstance().reference.child("images").child(imageName)
                uploadedImageReference.downloadUrl.addOnCompleteListener {
                    val downloadUrl = it.result.toString()
                    val currentUserEmail = auth.currentUser!!.email.toString()
                    val userComment = binding.commentPT.text.toString()
                    val date = com.google.firebase.Timestamp.now()

                    // database
                    val postMapHash = hashMapOf<String, Any>()
                    postMapHash["imageUrl"] = downloadUrl
                    postMapHash["userEmail"] = currentUserEmail
                    postMapHash["userComment"] = userComment
                    postMapHash["date"] = date

                    database.collection("ImageCollection").add(postMapHash).addOnCompleteListener {
                        if (it.isSuccessful){
                            finish()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_LONG).show()
                    }

                }
            }.addOnFailureListener {
                Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }


    fun selectImage(view: View) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }else {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, 2)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // similar things will be done
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, 2)
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {

            selectedImage = data.data

            if (selectedImage != null) {

                if (Build.VERSION.SDK_INT >= 28) {

                    val source = ImageDecoder.createSource(this.contentResolver, selectedImage!!)
                    selectedBitMap = ImageDecoder.decodeBitmap(source)
                    binding.imageView.setImageBitmap(selectedBitMap)

                } else{
                    selectedBitMap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                    binding.imageView.setImageBitmap(selectedBitMap)
                }


            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}
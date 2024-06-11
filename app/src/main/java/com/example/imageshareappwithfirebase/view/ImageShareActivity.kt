package com.example.imageshareappwithfirebase.view

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
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.imageshareappwithfirebase.R
import com.example.imageshareappwithfirebase.databinding.ActivityImageShareBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.UUID

class ImageShareActivity : AppCompatActivity() {

    var selectedImage : Uri? = null
    var selectedBitMap : Bitmap? = null
    private lateinit var binding : ActivityImageShareBinding
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private var userAddress : String? = null
    private var userCityName : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageShareBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*
        if (!OpenCVLoader.initDebug()) {
            // OpenCV başlatılamadı
            Toast.makeText(this, "OpenCV başlatılamadı", Toast.LENGTH_LONG).show()
        }
        */


        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        // load cities to spinner
        setupCitySpinner()

    }

    private fun setupCitySpinner() {
        val cities = resources.getStringArray(R.array.category_list)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.selectProductCategory.adapter = adapter
    }

    fun share(view: View) {

        val referance = storage.reference

        // we should use UUID
        val uuid = UUID.randomUUID()
        val imageName = "${uuid}.jpg"

        val imageReference = referance.child("images").child(imageName)

        if (selectedImage != null) {

            imageReference.putFile(selectedImage!!)
                .addOnSuccessListener {
                val uploadedImageReference = FirebaseStorage.getInstance().reference.child("images").child(imageName)
                uploadedImageReference.downloadUrl.addOnCompleteListener {
                    val downloadUrl = it.result.toString()
                    val currentUserEmail = auth.currentUser!!.email.toString()
                    val userComment = binding.commentPT.text.toString()
                    val date = com.google.firebase.Timestamp.now()
                    val productName = binding.productNameET.text.toString()
                    val productPrice = binding.productPrice.text.toString()

                    // database
                    val postMapHash = hashMapOf<String, Any>()
                    postMapHash["imageUrl"] = downloadUrl
                    postMapHash["userEmail"] = currentUserEmail
                    postMapHash["userComment"] = userComment
                    postMapHash["userAddress"] = userAddress.toString()
                    postMapHash["userCityName"] = userCityName.toString()
                    postMapHash["date"] = date
                    postMapHash["productName"] = productName
                    postMapHash["productPrice"] = productPrice

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
                try {
                    selectedBitMap = if (Build.VERSION.SDK_INT >= 28) {
                        val source = ImageDecoder.createSource(this.contentResolver, selectedImage!!)
                        ImageDecoder.decodeBitmap(source)
                    } else {
                        MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                    }
                    binding.imageView.setImageBitmap(selectedBitMap)
                    // added for text recognition
                    processImageForTextRecognition(selectedBitMap!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                // Start MapsActivity to get the address
                val intent = Intent(this, MapsActivity::class.java)
                startActivityForResult(intent, 3)
                // added for test
                finish()
            }
        }else if(requestCode == 3 && resultCode == Activity.RESULT_OK && data != null) {
            // Get address from MapsActivity result
            val address = data.getStringExtra("address")
            val cityName = data.getStringExtra("cityName")
            if (address != null && cityName != null) {
                // Now you have the address, proceed with sharing
                userAddress = address
                userCityName = cityName
            } else {
                Toast.makeText(this, "Adres alınamadı", Toast.LENGTH_LONG).show()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun processImageForTextRecognition(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                extractTextFromImage(visionText)  // **Eklenen kısım**
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    private fun extractTextFromImage(visionText: Text) {
        var recognizedText = ""
        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                recognizedText += line.text + "\n"
            }
        }
        binding.commentPT.setText(recognizedText)
    }



}
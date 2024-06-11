package com.example.imageshareappwithfirebase.view


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.imageshareappwithfirebase.R
import com.example.imageshareappwithfirebase.adapter.CategoryAdapter
import com.example.imageshareappwithfirebase.adapter.FeedRecyclerAdapter
import com.example.imageshareappwithfirebase.databinding.ActivityFeedBinding
import com.example.imageshareappwithfirebase.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FeedActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private lateinit var recyclerViewAdapter: FeedRecyclerAdapter
    private lateinit var binding: ActivityFeedBinding

    var postList = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        getDatas()

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        recyclerViewAdapter = FeedRecyclerAdapter(postList)
        binding.recyclerView.adapter = recyclerViewAdapter

        // Kategori listesini al
        val categoryList = resources.getStringArray(R.array.category_list).toList()

        // Kategori adapter'ını oluştur
        val categoryAdapter = CategoryAdapter(categoryList)
        binding.categoriesRV.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.categoriesRV.adapter = categoryAdapter

        binding.homeTV.setOnClickListener {
            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
        }

        binding.shareImage.setOnClickListener {
            val intent = Intent(this, ImageShareActivity::class.java)
            startActivity(intent)
        }

        binding.logOut.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun getDatas() {
        //val userCity = "Ankara"
        val currentUser = auth.currentUser
        val imageCollection = database.collection("ImageCollection")
        val userCollection = database.collection("Users")

        if (currentUser != null) {
            userCollection
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userCity = document.getString("city")
                        if (userCity != null) {
                            imageCollection
                                .whereEqualTo("userCityName", userCity)
                                .orderBy("productPrice", Query.Direction.ASCENDING)
                                .addSnapshotListener { value, error ->
                                    if (error != null) {
                                        Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
                                    }

                                    if (value != null && !value.isEmpty) {
                                        val documents = value.documents

                                        postList.clear()

                                        for (document in documents) {
                                            val userEmail = document.getString("userEmail") ?: ""
                                            val userComment = document.getString("userComment") ?: ""
                                            val imageUrl = document.getString("imageUrl") ?: ""
                                            val userAddress = document.getString("userAddress") ?: ""

                                            val downloadedPost = Post(userEmail, imageUrl, userComment, userAddress)
                                            postList.add(downloadedPost)
                                        }

                                        recyclerViewAdapter.notifyDataSetChanged()

                                    }

                                }
                        } else {
                            Toast.makeText(this, "User city not found", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this, "User document does not exist", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "No user signed in", Toast.LENGTH_LONG).show()
        }

    }
}



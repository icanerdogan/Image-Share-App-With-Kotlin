package com.example.imageshareappwithfirebase.view


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.imageshareappwithfirebase.R
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

    }

    fun getDatas() {

        database.collection("ImageCollection")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->

                if (error != null) {
                    Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
                }

                if (value != null && !value.isEmpty) {

                    val documents = value.documents

                    postList.clear()

                    for (document in documents) {
                        val userEmail = document.get("userEmail") as String
                        val userComment = document.get("userComment") as String
                        val imageUrl = document.get("imageUrl") as String

                        val downloadedPost = Post(userEmail, imageUrl, userComment)
                        postList.add(downloadedPost)
                    }

                    recyclerViewAdapter.notifyDataSetChanged()

                }

            }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.options_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.shareImage) {

            // going to activity of image share
            val intent = Intent(this, ImageShareActivity::class.java)
            startActivity(intent)

        } else if (item.itemId == R.id.logOut) {

            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        }

        return super.onOptionsItemSelected(item)
    }
}
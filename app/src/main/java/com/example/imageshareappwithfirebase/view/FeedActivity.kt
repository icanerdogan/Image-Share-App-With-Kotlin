package com.example.imageshareappwithfirebase.view


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imageshareappwithfirebase.R
import com.example.imageshareappwithfirebase.adapter.FeedRecyclerAdapter
import com.example.imageshareappwithfirebase.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FeedActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseFirestore
    private lateinit var recyclerViewAdapter: FeedRecyclerAdapter

    var postList = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        auth = FirebaseAuth.getInstance()
        storage = FirebaseFirestore.getInstance()

        getDatas()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = FeedRecyclerAdapter(postList)
    }

    fun getDatas() {

        storage.collection("ImageColletion")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
            if (error != null) {
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
            }

            if (snapshot != null && !snapshot.isEmpty) {

                postList.clear()

                val documents = snapshot.documents
                for (document in documents) {

                    val userEmail = document.get("userEmail") as String
                    val imageUrl = document.get("imageUrl") as String
                    val userComment = document.get("userComment") as String

                    val downloadPost = Post(userEmail, imageUrl, userComment)
                    postList.add(downloadPost)

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
package com.example.imageshareappwithfirebase.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.imageshareappwithfirebase.R
import com.example.imageshareappwithfirebase.model.Post
import com.squareup.picasso.Picasso


class FeedRecyclerAdapter(var postList: ArrayList<Post>): RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder>() {
    class  PostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userAddress: TextView = itemView.findViewById(R.id.recyclerRowAddress)
        var userCommentTextView: TextView = itemView.findViewById(R.id.recyclerRowComment)
        var imageUrlImageView: ImageView = itemView.findViewById(R.id.recyclerRowImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_row, parent, false)

        return PostHolder(view)
    }

    override fun getItemCount(): Int {

        return postList.size

    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {

        holder.userAddress.text = "Get Direction"//postList[position].userAddress
        holder.userCommentTextView.text = postList[position].userComment
        Picasso.get().load(postList[position].imageUrl).into(holder.imageUrlImageView)

        holder.userAddress.setOnClickListener {
            val address = postList[position].userAddress

            // Check if address is not null or empty
            if (address != null && address.isNotEmpty()) {
                val uri = Uri.parse("http://maps.google.com/maps?daddr=$address")
                val intent = Intent(Intent.ACTION_VIEW, uri)

                // Check if map app exists before starting the intent
                if (intent.resolveActivity(it.context.packageManager) != null) {
                    it.context.startActivity(intent)
                } else {
                    Toast.makeText(it.context, "Google Maps uygulaması bulunamadı!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(it.context, "Adres bilgisi mevcut değil!", Toast.LENGTH_SHORT).show()
            }
        }
    }




}
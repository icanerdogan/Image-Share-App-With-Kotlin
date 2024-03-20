package com.example.imageshareappwithfirebase.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.imageshareappwithfirebase.R
import com.example.imageshareappwithfirebase.model.Post
import com.squareup.picasso.Picasso


class FeedRecyclerAdapter(val postList: ArrayList<Post>): RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder>() {
    class  PostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userEmailTextView: TextView = itemView.findViewById(R.id.recyclerRowEmail)
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

        holder.userEmailTextView.text = postList[position].userEmail
        holder.userCommentTextView.text = postList[position].userComment
        Picasso.get().load(postList[position].imageUrl).into(holder.imageUrlImageView)

    }
}
package com.example.imageshareappwithfirebase.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.imageshareappwithfirebase.R
import com.example.imageshareappwithfirebase.model.Post

class FeedRecyclerAdapter(val postList: ArrayList<Post>): RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder>() {

    class  PostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

        holder.itemView.findViewById<TextView>(R.id.recyclerRowEmail).text = postList[position].userEmail
        holder.itemView.findViewById<TextView>(R.id.recyclerRowComment).text = postList[position].userComment
    }
}
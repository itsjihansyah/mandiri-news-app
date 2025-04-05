package com.example.mandirinewsapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mandirinewsapp.R
import com.example.mandirinewsapp.models.Article
import com.example.mandirinewsapp.ui.NewsDetail
import java.io.Serializable

class RecyclerViewAdapter(private val data: MutableList<Article>) :
    RecyclerView.Adapter<RecyclerViewAdapter.Holder>() {

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageNews: ImageView = itemView.findViewById(R.id.image)
        val title: TextView = itemView.findViewById(R.id.title)
        val author: TextView = itemView.findViewById(R.id.author)
        val date: TextView = itemView.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val article = data[position]

        holder.title.text = article.title
        holder.author.text = article.author ?: "Unknown"
        holder.date.text = article.publishedAt?.split("T")?.get(0) ?: "Unknown Date"

        holder.title.setOnClickListener {
            val intent = Intent(holder.itemView.context, NewsDetail::class.java)
            intent.putExtra("article", article)
            holder.itemView.context.startActivity(intent)
        }

        // Show image with Glide
        Glide.with(holder.itemView.context)
            .load(article.urlToImage)
            .placeholder(R.drawable.no_images)
            .into(holder.imageNews)
    }

    fun addData(newData: List<Article>) {
        val startPosition = data.size
        data.addAll(newData)
        notifyItemRangeInserted(startPosition, newData.size)
    }

    fun updateData(newList: List<Article>) {
        data.clear()
        data.addAll(newList)
        notifyDataSetChanged()
    }
}

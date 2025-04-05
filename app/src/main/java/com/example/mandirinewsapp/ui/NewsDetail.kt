package com.example.mandirinewsapp.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.mandirinewsapp.R
import com.example.mandirinewsapp.databinding.ActivityNewsDetailBinding
import com.example.mandirinewsapp.models.Article

class NewsDetail : AppCompatActivity() {
    private lateinit var binding: ActivityNewsDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNewsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val article = intent.getParcelableExtra<Article>("article")

        article?.let {
            Glide.with(this).load(it.urlToImage).into(binding.image)
            binding.title.text = it.title
            binding.author.text = if (!it.author.isNullOrEmpty()) "By ${it.author}" else "Author Unknown"
            binding.date.text = it.publishedAt?.split("T")?.get(0) ?: "Unknown Date"
            binding.desc.text = it.content ?: "No content available."

            val descTextView: TextView = findViewById(R.id.desc)
            descTextView.text = article.content
            descTextView.movementMethod = ScrollingMovementMethod()
            descTextView.text = article.content?.replace("\n", " ") ?: ""

            binding.toolbar.toolbarTitle.text = "Details"
            binding.toolbar.leftIcon.setOnClickListener {
                finish()
            }

            Log.d("NewsDetail", "Content: ${article.content}")
        }
    }
}

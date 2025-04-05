//  This project is owned by Jihan Syahira. Do not reuse the code for similar internship project submissions.
package com.example.mandirinewsapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mandirinewsapp.R
import com.example.mandirinewsapp.adapters.RecyclerViewAdapter
import com.example.mandirinewsapp.api.ConfigNetwork
import com.example.mandirinewsapp.databinding.ActivityNewsBinding
import com.example.mandirinewsapp.models.Article
import com.example.mandirinewsapp.models.ResponseData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.app.AlertDialog
import android.view.LayoutInflater
import com.example.mandirinewsapp.util.Constants

class NewsActivity : AppCompatActivity() {
    private var binding: ActivityNewsBinding? = null
    private var adapter: RecyclerViewAdapter? = null
    private val articleList: MutableList<Article> = ArrayList()
    private var isLoading = false
    private var currentPage = 1
    private val pageSize = Constants.QUERY_PAGE_SIZE
    private var loadingDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        enableEdgeToEdge()

        binding!!.seeAll.setOnClickListener {
            val intent = Intent(this, AllNews::class.java)
            startActivity(intent)
        }

        val layoutManager = LinearLayoutManager(this)
        binding!!.recyclerNews.layoutManager = layoutManager
        adapter = RecyclerViewAdapter(articleList)
        binding!!.recyclerNews.adapter = adapter

        loadNews(currentPage)
        setupHeadlineCategoryClickListeners()

        binding!!.recyclerNews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                if (!isLoading && totalItemCount <= (lastVisibleItem + 3)) {
                    currentPage++
                    loadNews(currentPage)
                }
            }
        })

        loadHeadlines()
    }

    private fun loadNews(page: Int) {
        if (isLoading) return
        showLoading()
        isLoading = true

        ConfigNetwork.api.getNews(
            "indonesia",
            "2025-04-01",
            "publishedAt",
            "en",
            Constants.API_KEY,
            page,
            pageSize
        ).enqueue(object : Callback<ResponseData?> {
            override fun onResponse(call: Call<ResponseData?>, response: Response<ResponseData?>) {
                isLoading = false
                hideLoading()
                val newArticles = response.body()?.articles ?: emptyList()
                if (newArticles.isNotEmpty()) {
                    adapter?.addData(newArticles)
                }
            }

            override fun onFailure(call: Call<ResponseData?>, t: Throwable) {
                isLoading = false
                hideLoading()
                Log.d("error", t.localizedMessage ?: "Unknown error")
            }
        })
    }

    private fun loadHeadlines(category: String = "general") {
        showLoading()
        ConfigNetwork.api.getHeadlines("us", Constants.API_KEY, category)
            .enqueue(object : Callback<ResponseData?> {
                override fun onResponse(call: Call<ResponseData?>, response: Response<ResponseData?>) {
                    hideLoading()
                    if (response.isSuccessful && response.body() != null) {
                        val dataNews: List<Article> = response.body()!!.articles
                        if (dataNews.isNotEmpty()) {
                            val headline = dataNews[0]
                            binding!!.hTitle.text = headline.title
                            binding!!.hAuthor.text = headline.author ?: "Unknown Author"
                            binding!!.hAuthor.text = headline.publishedAt?.split("T")?.get(0) ?: "Unknown Date"

                            binding!!.hTitle.setOnClickListener {
                                val intent = Intent(this@NewsActivity, NewsDetail::class.java)
                                intent.putExtra("article", headline)
                                startActivity(intent)
                            }

                            Glide.with(this@NewsActivity)
                                .load(headline.urlToImage)
                                .placeholder(R.drawable.no_images)
                                .into(binding!!.hImage)
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseData?>, t: Throwable) {
                    hideLoading()
                    Log.d("error", t.localizedMessage ?: "Unknown error")
                }
            })
    }

    private fun setupHeadlineCategoryClickListeners() {
        val categories = listOf(
            binding!!.categoryAll,
            binding!!.categorySports,
            binding!!.categoryHealth,
            binding!!.categoryTechnology,
            binding!!.categoryScience,
            binding!!.categoryBusiness
        )

        for (category in categories) {
            category.setOnClickListener {
                val categoryText = category.text.toString().lowercase()
                val selectedCategory = if (categoryText == "all") "general" else categoryText
                updateHeadlineCategory(selectedCategory, category, categories)
            }
        }
    }

    private fun updateHeadlineCategory(category: String, selectedTextView: TextView, allTextViews: List<TextView>) {
        loadHeadlines(category)
        for (textView in allTextViews) {
            if (textView == selectedTextView) {
                textView.setTypeface(ResourcesCompat.getFont(this, R.font.poppins_bold))
                textView.setTextColor(ContextCompat.getColor(this, R.color.dark_blue))
            } else {
                textView.setTypeface(ResourcesCompat.getFont(this, R.font.poppins_reg))
                textView.setTextColor(ContextCompat.getColor(this, R.color.black))
            }
        }
    }

    private fun showLoading() {
        if (loadingDialog != null) return
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_loading, null)
        builder.setView(view)
        builder.setCancelable(false)
        loadingDialog = builder.create()
        loadingDialog?.show()
    }

    private fun hideLoading() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }
}


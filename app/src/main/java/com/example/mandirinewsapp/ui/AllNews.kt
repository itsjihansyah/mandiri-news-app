package com.example.mandirinewsapp.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mandirinewsapp.R
import com.example.mandirinewsapp.adapters.RecyclerViewAdapter
import com.example.mandirinewsapp.api.ConfigNetwork
import com.example.mandirinewsapp.databinding.ActivityAllNewsBinding
import com.example.mandirinewsapp.models.Article
import com.example.mandirinewsapp.models.ResponseData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.app.AlertDialog
import android.view.LayoutInflater
import com.example.mandirinewsapp.util.Constants

class AllNews : AppCompatActivity() {
    private var binding: ActivityAllNewsBinding? = null
    private var adapter: RecyclerViewAdapter? = null
    private val articleList: MutableList<Article> = ArrayList()
    private val filteredList: MutableList<Article> = ArrayList()
    private var isLoading = false
    private var currentPage = 1
    private val pageSize = Constants.QUERY_PAGE_SIZE
    private var loadingDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllNewsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.toolbar.toolbarTitle.text = "All News"

        binding!!.toolbar.leftIcon.setOnClickListener {
            finish()
        }

        enableEdgeToEdge()

        val layoutManager = LinearLayoutManager(this)
        binding!!.recyclerNews.layoutManager = layoutManager
        adapter = RecyclerViewAdapter(articleList)
        binding!!.recyclerNews.adapter = adapter

        loadNews(currentPage)

        // Infinite scrolling
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

        binding!!.searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterNews(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
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
            "f0463e6c23054d68ae094a69ea9fed37",
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

    private fun filterNews(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(articleList)
        } else {
            filteredList.addAll(articleList.filter {
                it.title.contains(query, ignoreCase = true) ||
                        (it.description?.contains(query, ignoreCase = true) ?: false)
            })
        }
        adapter?.updateData(filteredList)
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

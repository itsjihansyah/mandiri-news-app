package com.example.mandirinewsapp.api

import com.example.mandirinewsapp.models.ResponseData
import com.example.mandirinewsapp.util.Constants.Companion.API_KEY
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsService {
    @GET("v2/top-headlines")
    fun getHeadlines(
        @Query("country") country: String,
        @Query("apiKey") apiKey: String = API_KEY,
        @Query("category") category: String
    ): Call<ResponseData>

    @GET("v2/everything")
    fun getNews(
        @Query("q") q: String,
        @Query("from") from: String,
        @Query("sortBy") sortBy: String,
        @Query("language") language: String,
        @Query("apiKey") apiKey: String = API_KEY,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Call<ResponseData>
}

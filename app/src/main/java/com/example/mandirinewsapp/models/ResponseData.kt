package com.example.mandirinewsapp.models

class ResponseData(
    var articles: MutableList<Article>,
    var status: String,
    var totalResults: Int
)

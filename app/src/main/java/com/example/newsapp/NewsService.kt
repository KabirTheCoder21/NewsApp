package com.example.newsapp

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

//https://newsapi.org/v2/top-headlines?country=in&apiKey=104d3905eeac4a939943f8c67ac0fd61
const val BASE_URL = "https://newsapi.org/v2/"
const val API_KEY = "104d3905eeac4a939943f8c67ac0fd61"
interface NewsInterface {
    @GET("top-headlines?apiKey=$API_KEY")
    fun getHeadLines(
        @Query("country")country : String,
        @Query("page")page : Int) : Call<News>
}
object NewsService{
    val newsInstance : NewsInterface
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        newsInstance = retrofit.create(NewsInterface::class.java)
    }
}
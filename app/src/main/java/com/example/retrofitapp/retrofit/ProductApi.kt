package com.example.retrofitapp.retrofit

import retrofit2.http.GET
import retrofit2.http.Path

interface ProductApi {
    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Product
}
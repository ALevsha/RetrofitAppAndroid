package com.example.retrofitapp.retrofit

import com.example.retrofitapp.retrofit.models.AuthRequest
import com.example.retrofitapp.retrofit.models.Product
import com.example.retrofitapp.retrofit.models.Products
import com.example.retrofitapp.retrofit.models.User
import retrofit2.http.*

interface MainApi {
    @POST("auth/login")
    suspend fun auth(@Body authRequest: AuthRequest): User

    @GET("auth/products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Product

    @GET("auth/products")
    suspend fun getAllProducts() : Products

    //Headers - заголовок, передающийся со всеми запросами, он не изменяется
    @Headers("Content-Type: application/json")
    @GET("auth/products/search")
    suspend fun getProductsByNameAsAuthUser(
        //добавочный Header - можно добовлять по мере надобности
        @Header("Authorization") token: String,
        @Query("q") name: String) : Products
}
package com.example.retrofitapp.retrofit.models

data class Products(
    val limit: Int,
    val products: List<Product>,
    val skip: Int,
    val total: Int
)
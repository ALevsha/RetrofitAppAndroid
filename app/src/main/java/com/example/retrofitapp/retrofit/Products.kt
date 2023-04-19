package com.example.retrofitapp.retrofit

data class Products(
    val limit: Int,
    val products: List<Product>,
    val skip: Int,
    val total: Int
)
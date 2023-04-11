package com.example.retrofitapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.retrofitapp.retrofit.ProductApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv1 = findViewById<TextView>(R.id.tv1)
        val b1 = findViewById<Button>(R.id.b1)
        val et = findViewById<EditText>(R.id.etNumber)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://dummyjson.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val productApi = retrofit.create(ProductApi::class.java)

        b1.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {

                val product = productApi.getProductById(id = if (et.text.isEmpty()) 1
                                                            else et.text.toString().toInt())
                runOnUiThread{
                    tv1.text = product.brand
                }
            }
        }
    }
}
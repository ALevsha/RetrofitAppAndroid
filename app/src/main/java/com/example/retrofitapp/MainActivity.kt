package com.example.retrofitapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrofitapp.adapter.ProductAdapter
import com.example.retrofitapp.databinding.ActivityMainBinding
import com.example.retrofitapp.retrofit.MainApi
import com.example.retrofitapp.retrofit.models.AuthRequest
import com.example.retrofitapp.retrofit.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ProductAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Guest"

        adapter = ProductAdapter()
        binding.rcView.layoutManager = LinearLayoutManager(this)
        binding.rcView.adapter = adapter


        //Http logging interceptor
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://dummyjson.com")
            .client(client)//client includes in retrofit for logging API actions
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val mainApi = retrofit.create(MainApi::class.java)


        var user: User? = null

        CoroutineScope(Dispatchers.IO).launch {
            user = mainApi.auth(
                AuthRequest(
                    "kminchelle",
                    "0lelplR"
                )
            )
            runOnUiThread {
                supportActionBar?.title = user?.firstName
            }
        }


        //searchView - компонент строки поиска в android
        binding.sv.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(key: String?): Boolean {
                //запрос выполняется при нажатии на кнопку поиска
                return true
            }

            override fun onQueryTextChange(key: String?): Boolean {
                //запрос выполняется при изменении текста запроса
                CoroutineScope(Dispatchers.IO).launch {
                    val products = key?.let {
                        mainApi.getProductsByNameAsAuthUser(user?.token ?: "", it)
                    }
                    runOnUiThread {
                        binding.apply {
                            adapter.submitList(products?.products)
                        }
                    }
                }
                return true
            }
        })
    }
}
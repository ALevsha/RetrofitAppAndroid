package com.example.retrofitapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.retrofitapp.databinding.ActivityMainBinding
import com.example.retrofitapp.retrofit.AuthRequest
import com.example.retrofitapp.retrofit.MainApi
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        //Http logging interceptor
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://dummyjson.com")
            .client(client)//client includes in retrofit for logging API actions
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val MainApi = retrofit.create(MainApi::class.java)

        binding.bSign.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {

                val user = MainApi.auth(
                    AuthRequest(
                        binding.etUsername.text.toString(),
                        binding.etPassword.text.toString()
                    )
                )
                runOnUiThread{
                    binding.apply {
                        tvFirstName.text = user.firstName
                        tvLastName.text = user.lastName
                        //getting image with Picasso, including this image in ImageView
                        Picasso.get().load(user.image).into(avatar)
                    }
                }
            }
        }
    }
}
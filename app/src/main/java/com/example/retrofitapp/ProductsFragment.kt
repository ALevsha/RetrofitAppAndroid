package com.example.retrofitapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrofitapp.adapter.ProductAdapter
import com.example.retrofitapp.databinding.FragmentProductsBinding
import com.example.retrofitapp.retrofit.MainApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductsFragment : Fragment() {

    private lateinit var binding: FragmentProductsBinding
    private val viewModel: LoginViewModel by activityViewModels()

    private lateinit var mainApi: MainApi
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRetrofit()
        initRecView()
        viewModel.token.observe(viewLifecycleOwner) { token ->
            CoroutineScope(Dispatchers.IO).launch {
                val products = mainApi.getAllProducts(token)
                requireActivity().runOnUiThread {
                    binding.apply {
                        adapter.submitList(products.products)
                    }
                }
            }
        }
    }

    private fun initRecView() {
        adapter = ProductAdapter()
        binding.rcView.layoutManager = LinearLayoutManager(context)
        binding.rcView.adapter = adapter
    }

    private fun initRetrofit() {
        //Http logging interceptor
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://dummyjson.com")
            .client(client)//client includes in retrofit for logging API actions
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        mainApi = retrofit.create(MainApi::class.java)
    }

}
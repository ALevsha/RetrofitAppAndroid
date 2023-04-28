package com.example.retrofitapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.retrofitapp.databinding.FragmentLoginBinding
import com.example.retrofitapp.retrofit.MainApi
import com.example.retrofitapp.retrofit.models.AuthRequest
import com.example.retrofitapp.retrofit.models.User
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by activityViewModels()
    private lateinit var mainApi: MainApi

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRetrofit()


        binding.etUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.tvError.isVisible or binding.bNext.isVisible) {
                    binding.tvError.visibility = View.GONE
                    binding.bNext.visibility = View.GONE
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.tvError.isVisible or binding.bNext.isVisible) {
                    binding.tvError.visibility = View.GONE
                    binding.bNext.visibility = View.GONE
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })


        binding.bSingin.setOnClickListener {
            auth(
                AuthRequest(
                    binding.etUsername.text.toString(),
                    binding.etPassword.text.toString()
                )
            )

        }


        binding.bNext.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_productsFragment)
        }
    }


    private fun auth(authRequest: AuthRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = mainApi.auth(authRequest)
            val message = response.errorBody()?.string()?.let {
                JSONObject(it).getString("message")

            }
            if (message != null)
                requireActivity().runOnUiThread {
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvError.text = message
                }
            else {
                val user = response.body()
                if (user != null) {
                    requireActivity().runOnUiThread {
                        binding.bNext.visibility = View.VISIBLE
                        Picasso.get().load(user.image).into(binding.ivAvatar)
                        binding.tvName.text = user.firstName
                        viewModel.token.value = user.token
                    }
                }
            }
        }
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

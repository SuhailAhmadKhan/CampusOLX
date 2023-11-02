package com.example.campusolx.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campusolx.RetrofitInstance
import com.example.campusolx.activites.AdDetailsActivity
import com.example.campusolx.adapters.AdapterAd
import com.example.campusolx.data.AuthDatabase
import com.example.campusolx.data.AuthTokenRepository
import com.example.campusolx.dataclass.Product
import com.example.campusolx.databinding.FragmentMyAdsBinding
import com.example.campusolx.interfaces.ProductApi
import com.example.campusolx.models.ModelAd
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class MyAdsFragment : Fragment(), AdapterAd.OnAdClickListener {

    private lateinit var binding: FragmentMyAdsBinding
    private lateinit var mContext: Context
    private lateinit var productApi: ProductApi
    private lateinit var adapterAd: AdapterAd
    private lateinit var authTokenRepository: AuthTokenRepository
    private var adArrayList: ArrayList<ModelAd> = ArrayList()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyAdsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the Retrofit ProductApi
        val retrofit = RetrofitInstance.getRetrofitInstance()
        productApi = retrofit.create(ProductApi::class.java)

        // Initialize the Room authToken repository
        authTokenRepository =
            AuthTokenRepository(AuthDatabase.getDatabase(requireContext()).authTokenDao())

        // Setup RecyclerView and Adapter
        adapterAd = AdapterAd(mContext, adArrayList)
        binding.adsRv.layoutManager = LinearLayoutManager(mContext)
        binding.adsRv.adapter = adapterAd

        // Set the listener for click events
        adapterAd.setOnAdClickListener(this@MyAdsFragment)

        // Fetch and display the products using the retrieved access token
        fetchMyAds()
    }

    override fun onAdClick(productId: String) {
        // Create an intent to open the AdDetailsActivity
        val intent = Intent(mContext, AdDetailsActivity::class.java)
        intent.putExtra("product_id", productId)
        startActivity(intent)
    }

    private fun fetchMyAds() {
        com.example.campusolx.utils.AdLoader.showDialog(mContext, isCancelable = true)

        lifecycleScope.launch {
            // Get the access token from Room database
            val authToken = authTokenRepository.getAuthToken()
            val accessToken = "Bearer " + (authToken?.token ?: "")

            // Get the user ID from Room database
            val userId = authTokenRepository.getUserId() ?: ""

            val call = productApi.getAllProductsOfUser(accessToken, userId)
            call.enqueue(object : Callback<List<Product>> {
                override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                    com.example.campusolx.utils.AdLoader.hideDialog()
                    if (response.isSuccessful) {
                        val productList = response.body()
                        productList?.let { products ->
                            adArrayList.clear()
                            for (product in products) {
                                val ad = ModelAd().apply {
                                    id = product._id
                                    uid = product.createdBy
                                    brand = product.name
                                    category = product.category
                                    price = product.price.toString()
                                    title = product.name
                                    description = product.description
                                    status = if (product.isSold) "Sold" else "Available"
                                    timestamp = product.createdAt?.let {
                                        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")
                                        dateFormat.parse(it)?.time ?: 0L
                                    } ?: 0L
                                    latitude = 0.0
                                    longitude = 0.0
                                    imageList = ArrayList(product.images)
                                }
                                adArrayList.add(ad)
                            }
                            adapterAd.notifyDataSetChanged()
                        }
                    } else {
                        // Handle error case
                        val errorMessage = response.message()
                        Log.e("Fetch", "Failed to fetch products: $errorMessage")
                        Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                    com.example.campusolx.utils.AdLoader.hideDialog()
                    Log.e("Fetch", "Failed to fetch products: ${t.message}")
                    Toast.makeText(
                        context,
                        "Failed to fetch products: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }
}
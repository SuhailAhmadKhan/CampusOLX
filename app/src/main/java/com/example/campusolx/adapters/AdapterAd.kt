package com.example.campusolx.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusolx.R
import com.example.campusolx.databinding.RowAdBinding
import com.example.campusolx.models.ModelAd
import com.example.campusolx.utils.Utils
import kotlinx.coroutines.CoroutineScope

// Adapter for displaying ads in a RecyclerView
class AdapterAd(private val context: Context, private val adArrayList: List<ModelAd>) :
    RecyclerView.Adapter<AdapterAd.HolderAd>() {

    // Interface to handle ad item click events
    interface OnAdClickListener {
        fun onAdClick(productId: String)
    }

    private var onAdClickListener: OnAdClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderAd {
        val binding = RowAdBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderAd(binding)
    }

    override fun onBindViewHolder(holder: HolderAd, position: Int) {
        val modelAd = adArrayList[position]

        // Load the first image of the ad (if available) using Glide
        loadAdFirstImage(modelAd, holder)

        // Set ad details to the ViewHolder's views
        holder.bind(modelAd)

        // Handle item click events
        holder.itemView.setOnClickListener {
            val productId = modelAd.id
            if (productId != null) {
                onAdClickListener?.onAdClick(productId)
            }
        }
    }

    // Set a click listener for ad item clicks
    fun setOnAdClickListener(listener: OnAdClickListener?) {
        this.onAdClickListener = listener
    }

    private fun loadAdFirstImage(modelAd: ModelAd, holder: HolderAd) {
        val imageList = modelAd.imageList

        if (imageList.isNotEmpty()) {
            val firstImageUrl = imageList[0] // Assuming the first image URL is at index 0
            // Load the first image into the ImageView using Glide
            // Replace 'placeholderImage' with the default image resource to display while loading
            Glide.with(context)
                .load(firstImageUrl)
                .placeholder(R.drawable.ic_person)
                .into(holder.binding.imageTv)
        } else {
            // If no images are available, you can set a placeholder image or hide the ImageView
            holder.binding.imageTv.setImageResource(R.drawable.ic_person)
        }
    }

    override fun getItemCount(): Int = adArrayList.size

    inner class HolderAd(val binding: RowAdBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(modelAd: ModelAd) {
            binding.apply {
                titleTv.text = modelAd.title
                descriptionTv.text = modelAd.description
                priceTv.text = modelAd.price
                dateTv.text = Utils.formatTimestampDate(modelAd.timestamp)
            }
        }
    }
}
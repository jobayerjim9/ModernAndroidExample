package com.jobaer.example.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.jobaer.example.R

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("imageUrl")
    fun loadImage(imageView: ImageView, imageUrl: String?) {
        imageUrl?.takeIf { it.isNotEmpty() }?.let {
            val requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
            Glide.with(imageView.context).load(it).apply(requestOptions).placeholder(R.drawable.ic_loader).into(imageView)
        }
    }
}
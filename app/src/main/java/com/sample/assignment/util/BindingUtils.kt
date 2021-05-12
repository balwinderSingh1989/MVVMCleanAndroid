package com.sample.assignment.util

import android.graphics.drawable.Drawable
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.target.Target
import com.google.android.material.textfield.TextInputLayout
import com.sample.assignment.R
import com.sample.assignment.util.image.GlideApp
import com.sample.core.utility.extensions.TAG
import com.sample.core.utility.logger.AppLogger
import java.io.File


@BindingAdapter("setUrl")
fun setImageUrl(imageView: AppCompatImageView, url: String?) {
    if (url.isNullOrEmpty().not()) {
        GlideApp.with(imageView.context)
            .load(url).circleCrop()
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    exception: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    AppLogger.d(TAG , "Loading image from datasource = $dataSource")
                    imageView.setImageDrawable(resource)
                    return true
                }
            }).diskCacheStrategy(DiskCacheStrategy.DATA)
            .preload()
    }
}


@BindingAdapter("setFile")
fun setFile(imageView: AppCompatImageView, file: File?) {
    file?.let {
        Glide.with(imageView.context)
            .load(it).circleCrop()
            .skipMemoryCache(true)
            .placeholder(
                (R.drawable.circle_grey_light_filled)
            )
            .into(DrawableImageViewTarget(imageView))

    }


    @BindingAdapter("setError")
    fun setError(editText: TextInputLayout, str: String?) {
        if (!str.isNullOrEmpty()) {
            editText.error = str
        }
    }

}

fun setRequestOptions(): RequestOptions {
    return RequestOptions().onlyRetrieveFromCache(true)
}





package com.humbjorch.restaurantapp.core.utils

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

class Extensions {
    companion object{
        fun ImageView.loadImageUrl(resource: String?) {
            if (resource != null) {
                val options: RequestOptions = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()

                Glide.with(this.context)
                    .load(resource)
                    .apply(options)
                    .into(this)
            }
        }
    }

}
fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}

fun View.showHide(isVisible:Boolean) {
    if (isVisible)
        this.visibility = View.VISIBLE
    else
       this.visibility = View.GONE
}
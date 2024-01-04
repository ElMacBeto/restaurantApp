package com.humbjorch.restaurantapp.core.utils

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

class Extensions {
    companion object{
        fun CircleImageView.loadImageUrl(resource: String?) {
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
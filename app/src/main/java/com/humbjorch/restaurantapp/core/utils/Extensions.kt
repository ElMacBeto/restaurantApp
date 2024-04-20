package com.humbjorch.restaurantapp.core.utils

import android.content.DialogInterface
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Parcelable
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.core.utils.alerts.GenericDialog

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

fun View.showOrInvisible(isVisible: Boolean) {
    if (isVisible)
        this.visibility = View.VISIBLE
    else
        this.visibility = View.INVISIBLE
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

fun View.isVisible(visible:Boolean){
    if (visible)
        this.visibility = View.VISIBLE
    else
        this.visibility = View.GONE
}

fun AppCompatActivity.genericAlert(
    imageAlert: Int = R.drawable.generic_icon_warning,
    titleAlert: String,
    descriptionAlert: String,
    txtBtnPositiveAlert: String,
    txtBtnNegativeAlert: String,
    isCancelableAlert: Boolean = false,
    buttonPositiveAction: (() -> Unit)? = null,
    buttonNegativeAction: (() -> Unit)? = null,
) {
    lifecycleScope.launchWhenResumed {
        GenericDialog().apply {
            imgDialog = imageAlert
            txtConfirm = txtBtnPositiveAlert
            txtCancel = txtBtnNegativeAlert
            txtTitle = titleAlert
            txtMessageAlert = descriptionAlert
            isCancelable = isCancelableAlert
            listener = object : GenericDialog.OnClickListener {
                override fun onClick(which: Int) {
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            buttonPositiveAction?.invoke()
                        }

                        else -> {
                            buttonNegativeAction?.invoke()
                        }
                    }
                }
            }
            this.show(supportFragmentManager, System.currentTimeMillis().toString())
        }
    }
}

fun AppCompatActivity.extShowLoader(loader: DialogFragment) {
    try {
        val loaderDialog = supportFragmentManager.findFragmentByTag("Loader")
        val isShowing = loader.dialog?.isShowing ?: false
        if (loaderDialog != null && loaderDialog.isAdded) {
            return
        }

        if (!loader.isAdded && !loader.isVisible && !isShowing) {
            loader.show(supportFragmentManager, "Loader")
            supportFragmentManager.executePendingTransactions()
        }
    } catch (e: Exception) {
        //ERROR
    }
}

fun AppCompatActivity.extDismissLoader(loader: DialogFragment) {
    if (loader.isAdded) {
        if (loader.isResumed) {
            loader.dismiss()
        } else {
            loader.dismissAllowingStateLoss()
        }
    }
}

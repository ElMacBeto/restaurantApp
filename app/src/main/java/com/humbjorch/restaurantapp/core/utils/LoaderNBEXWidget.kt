package com.humbjorch.restaurantapp.core.utils

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.humbjorch.restaurantapp.databinding.FragmentLoaderWidgetBinding

class LoaderNBEXWidget : DialogFragment() {
    private lateinit var binding: FragmentLoaderWidgetBinding


    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoaderWidgetBinding.inflate(
            LayoutInflater.from(context),
            container,
            false
        )
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        dialog?.setCancelable(false)
        window?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.transparent
                )
            )
        )
        window?.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
    }

    override fun onResume() {
        super.onResume()
        setImagesForAnimation()
    }

    override fun onPause() {
        binding.animationJSON.cancelAnimation()
        super.onPause()
    }

    private fun setImagesForAnimation() {
        binding.animationJSON.resumeAnimation()
    }

}
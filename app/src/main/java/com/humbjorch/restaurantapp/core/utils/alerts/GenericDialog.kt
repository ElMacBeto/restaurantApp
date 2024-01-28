package com.humbjorch.restaurantapp.core.utils.alerts

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.DialogFragment
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.core.utils.setSafeOnClickListener
import com.humbjorch.restaurantapp.databinding.FragmentGenericDialogBinding


class GenericDialog : DialogFragment() {
    private lateinit var binding: FragmentGenericDialogBinding

    var imgDialog: Int? = null
    lateinit var txtConfirm: String
    lateinit var txtCancel: String
    lateinit var txtTitle: String
    lateinit var txtMessageAlert: String
    var listener: OnClickListener? = null

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(0) as Drawable)
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }
        binding =
            FragmentGenericDialogBinding.inflate(LayoutInflater.from(context), container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
        this.setListeners()
    }

    private fun initComponents() {
        binding.imgGeneric.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                imgDialog!!
            )
        )

        binding.btnPositiveAlertGeneric.text = txtConfirm
        binding.btnNegativeAlertGeneric.text = txtCancel
        binding.txtTitleAlertGeneric.text = txtTitle
        binding.txtMessageAlertGeneric.text = txtMessageAlert
    }

    private fun setListeners() {
        binding.btnPositiveAlertGeneric.setSafeOnClickListener {
            listener?.onClick(DialogInterface.BUTTON_POSITIVE)
            dismiss()
        }

        binding.btnNegativeAlertGeneric.setSafeOnClickListener {
            listener?.onClick(DialogInterface.BUTTON_NEGATIVE)
            dismiss()
        }
    }

    interface OnClickListener {
        fun onClick(which: Int)
    }
}



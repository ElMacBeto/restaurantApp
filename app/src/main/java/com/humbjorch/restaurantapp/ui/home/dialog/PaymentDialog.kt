package com.humbjorch.restaurantapp.ui.home.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.humbjorch.restaurantapp.core.utils.OrderStatus
import com.humbjorch.restaurantapp.databinding.DialogPaymentBinding

class PaymentDialog(
    private val actionFunction: (OrderStatus) -> Unit
) : DialogFragment() {

    private lateinit var binding: DialogPaymentBinding
    private var orderStatus = OrderStatus.CASH_PAYMENT

    @SuppressLint("InflateParams", "SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogPaymentBinding.inflate(layoutInflater)

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            orderStatus = when (checkedId) {
                binding.rbCashPayment.id -> {
                    OrderStatus.CASH_PAYMENT
                }

                binding.rbCardPayment.id -> {
                    OrderStatus.CARD_PAYMENT
                }

                else -> {
                    OrderStatus.CASH_PAYMENT
                }
            }
        }

        binding.btnPayment.setOnClickListener {
            actionFunction.invoke(orderStatus)
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    companion object {
        const val TAG = "DeleteDialog"
    }

}
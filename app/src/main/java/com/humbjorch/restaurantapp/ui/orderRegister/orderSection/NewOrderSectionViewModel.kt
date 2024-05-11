package com.humbjorch.restaurantapp.ui.orderRegister.orderSection

import androidx.lifecycle.ViewModel
import com.humbjorch.restaurantapp.data.model.ExtraModel
import com.humbjorch.restaurantapp.data.model.ProductsOrderModel

class NewOrderSectionViewModel : ViewModel() {

    fun getProductPrice(productPrice: String, newExtras: List<ExtraModel>): Int {
        var result = productPrice.toInt()
        newExtras.forEach {
            result += it.price.toInt()
        }
        return result
    }

    fun updateOrder(
        newProductOrder: ProductsOrderModel,
        productList: List<ProductsOrderModel>?,
        action: (List<ProductsOrderModel>?) -> Unit
    ) {
        var resultProductList = productList
        var positionChanged = -1

        resultProductList?.onEachIndexed { index, it ->
            if (it.product == newProductOrder.product &&
                it.ingredients == newProductOrder.ingredients &&
                it.extras == newProductOrder.extras &&
                it.other == newProductOrder.other
            ) {
                positionChanged = index
                it.amount = (it.amount.toInt() + newProductOrder.amount.toInt()).toString()
            }
        }
        if (positionChanged == -1) {
            resultProductList = resultProductList?.plus(newProductOrder)
            action(resultProductList)
        }
    }


}
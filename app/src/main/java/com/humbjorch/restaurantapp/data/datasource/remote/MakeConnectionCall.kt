package com.humbjorch.restaurantapp.data.datasource.remote

import com.humbjorch.restaurantapp.core.utils.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext


suspend fun <T> makeCall(
    call: suspend () -> T
): Resource<T> = withContext(Dispatchers.IO) {
    try {
        Resource.success(call())
    } catch (error: Exception) {
        Resource.error(error.message)
    }
}


class Resource<T>(
    val status: Status,
    val data: T? = null,
    val message: String? = null,
) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data)
        }

        fun <T> error(msg: String? = null, data: T? = null): Resource<T> {
            return Resource(Status.ERROR, message = msg, data = data)
        }

        fun <T> loading(msg: Int? = null): Resource<T> {
            return Resource(Status.LOADING)
        }
    }
}
package com.humbjorch.restaurantapp.data.mappers

interface Mapper<I, O> {
    suspend fun map(input: I): O
}
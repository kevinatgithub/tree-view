package com.imu.flowerdelivery.network.callbacks

import com.imu.flowerdelivery.network.models.ApiList

interface ArrayResponseHandler<T> {
    fun onSuccess(collection: Array<T>)
    fun onError(error: String)
}
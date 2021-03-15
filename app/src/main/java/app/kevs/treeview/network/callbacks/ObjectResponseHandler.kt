package com.imu.flowerdelivery.network.callbacks

import com.imu.flowerdelivery.network.models.ResponseObject

interface ObjectResponseHandler<T> {
    fun onSuccess(obj: ResponseObject<T>)
    fun onError(error: String)
}
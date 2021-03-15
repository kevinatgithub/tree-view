package com.imu.flowerdelivery.network.models

import com.google.gson.annotations.SerializedName

class ApiList<T> {
    @SerializedName("data")
    var data: List<T>? = null
}
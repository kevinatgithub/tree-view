package com.imu.flowerdelivery.network.models

import com.google.gson.annotations.SerializedName

public class ResponseObject<T> {
    @SerializedName("data")
    public var Data : T? = null
}
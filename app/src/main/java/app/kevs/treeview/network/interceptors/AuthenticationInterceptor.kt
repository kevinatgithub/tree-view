package com.imu.flowerdelivery.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class AuthenticationInterceptor: Interceptor {
    constructor(authToken: String){
        this.authToken = authToken
    }

    private var authToken : String? = null
    override fun intercept(chain: Interceptor.Chain?): Response {
        val original = chain!!.request()

        val builder = original.newBuilder()
            .header("Authorization", "Bearer $authToken")
            .header("Accept", "application/json")


        val request = builder.build()
        return chain!!.proceed(request)
    }
}

package com.imu.flowerdelivery.network.interceptors

import android.content.Context
import android.net.ConnectivityManager
import com.imu.flowerdelivery.network.exceptions.NoConnectivityException
import com.imu.flowerdelivery.network.exceptions.ServerConnectionException
import okhttp3.Interceptor
import okhttp3.Response

public class ServerConnectionInterceptor : Interceptor {
    constructor(context : Context) {
        this.context = context
    }

    private var context: Context? = null

    override fun intercept(chain: Interceptor.Chain?): Response {
        if (!isConnected()) {
            throw NoConnectivityException()
        }

        val request = chain!!.request()
        val response = chain!!.proceed(request)

        if (response.code() >= 500 && response.code() < 600) {
            // NOTE: You can add additional handling here for Server Errors
            throw ServerConnectionException()
        }

        return response
    }

    fun isConnected(): Boolean {
        val connectivityManager = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }
}
package com.imu.flowerdelivery.network

import android.content.Context
import android.text.TextUtils
import com.imu.flowerdelivery.network.interceptors.AuthenticationInterceptor
import com.imu.flowerdelivery.network.interceptors.ServerConnectionInterceptor
import com.imu.flowerdelivery.network.interceptors.TokenAuthenticationInterceptor
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiGenerator {
    companion object {

        private val APP_BASE_URL: String = "https://tree-api.getsandbox.com/"
        private val httpClient = OkHttpClient.Builder()

        private val builder: Retrofit.Builder = Retrofit.Builder()
                .baseUrl(APP_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())

        private var retrofit: Retrofit = builder.build()

        fun <S> createService(
            serviceClass: Class<S>,
            username: String?,
            password: String,
            uniqueDeviceToken: String,
            context: Context
        ): S {
            var password = password
            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {

                if (!uniqueDeviceToken.equals("", ignoreCase = true)) {
                    password = "$password:$uniqueDeviceToken"
                }

                val authToken = Credentials.basic(username, password)
                val interceptor = TokenAuthenticationInterceptor(authToken)
                val connectionInterceptor = ServerConnectionInterceptor(context)

                if (!httpClient.interceptors().contains(interceptor) ) {
                    httpClient.addInterceptor(interceptor)
                    httpClient.addInterceptor(connectionInterceptor)
                    builder.client(httpClient.build())
                    retrofit = builder.build()
                }
            }
            return retrofit.create(serviceClass)
        }

        fun <S> createService(
            serviceClass: Class<S>,
            authToken: String,
            context: Context
        ): S {
            if (!TextUtils.isEmpty(authToken)) {

                val interceptor = AuthenticationInterceptor(authToken)
                val connectionInterceptor = ServerConnectionInterceptor(context)

                if (!httpClient.interceptors().contains(interceptor)) {
                    httpClient.addInterceptor(interceptor)
                    httpClient.addInterceptor(connectionInterceptor)
                    builder.client(httpClient.build())
                    retrofit = builder.build()
                }
            }
            return retrofit.create(serviceClass)
        }

        fun <S> createService(serviceClass: Class<S>, context: Context): S {
            builder.client(httpClient.build())
            retrofit = builder.build()
            return retrofit.create(serviceClass)
        }
    }
}
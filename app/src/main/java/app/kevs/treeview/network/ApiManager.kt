package com.imu.flowerdelivery.network

import android.content.Context
import app.kevs.treeview.network.ConfigApiGenerator
import app.kevs.treeview.network.interfaces.MigrateApi
import app.kevs.treeview.network.interfaces.TreeConfigApi
import com.imu.flowerdelivery.network.callbacks.ArrayResponseHandler
import com.imu.flowerdelivery.network.callbacks.ObjectResponseHandler
import com.imu.flowerdelivery.network.interfaces.TreeApi
import com.imu.flowerdelivery.network.models.ResponseObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiManager private  constructor(){
    companion object{
        private var apiManager: ApiManager? = null

        fun getManagerInstance(): ApiManager {
            if (apiManager == null) apiManager = ApiManager()
            return apiManager!!
        }

        fun getInstance(context: Context): TreeApi {
            return ApiGenerator.createService(TreeApi::class.java, context)
        }

        fun getConfigInstance(): TreeConfigApi {
            return ConfigApiGenerator.createServiceConfigApi(TreeConfigApi::class.java)
        }

        fun getMigrateInstance(): MigrateApi {
            return ConfigApiGenerator.createServiceMigrateApi(MigrateApi::class.java)
        }

        fun  <T>setArrayDefaultHandler(callback: ArrayResponseHandler<T>) : Callback<Array<T>> {
            return object : Callback<Array<T>>{
                override fun onFailure(call: Call<Array<T>>?, t: Throwable?) {
                    callback.onError(t!!.message!!)
                }

                override fun onResponse(
                    call: Call<Array<T>>?,
                    response: Response<Array<T>>?
                ) {
                    if (response!!.isSuccessful()) {
                        var arr : Array<T> = response.body()
                        callback.onSuccess(arr)
                    } else {
                        callback.onError(response.message())
                    }
                }

            }
        }

        fun  <C>setDefaultHandler(callback: ObjectResponseHandler<C>) : Callback<ResponseObject<C>> {
            return object : Callback<ResponseObject<C>>{
                override fun onFailure(call: Call<ResponseObject<C>>?, t: Throwable?) {
                    callback.onError(t!!.message!!)
                }

                override fun onResponse(
                    call: Call<ResponseObject<C>>?,
                    response: Response<ResponseObject<C>>?
                ) {
                    if (response!!.isSuccessful()) {
                        var responseObject : ResponseObject<C> = response.body()
                        callback.onSuccess(responseObject)
                    } else {
                        callback.onError(response.message())
                    }
                }

            }
        }
    }


}
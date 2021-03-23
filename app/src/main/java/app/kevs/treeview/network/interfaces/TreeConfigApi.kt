package app.kevs.treeview.network.interfaces

import app.kevs.treeview.network.models.ConfigDto
import com.imu.flowerdelivery.network.models.ResponseObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TreeConfigApi {
    @GET("config")
    fun getConfig(): Call<ResponseObject<ConfigDto>>

    @POST("config")
    fun postConfig(@Body data : ConfigDto): Call<ResponseObject<ConfigDto>>
}
package app.kevs.treeview.network.interfaces

import app.kevs.treeview.network.models.MigrateDto
import com.imu.flowerdelivery.network.models.ResponseObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface MigrateApi {

    @POST("TreeApiMigrateGetData")
    fun migrate(@Query("code") code : String, @Body migrateDto: MigrateDto) : Call<ResponseObject<MigrateDto>>
}
package app.kevs.treeview.helpers

import app.kevs.treeview.Constants
import app.kevs.treeview.network.models.ConfigDto
import com.imu.flowerdelivery.network.callbacks.ArrayResponseHandler
import com.imu.flowerdelivery.network.callbacks.ObjectResponseHandler
import com.imu.flowerdelivery.network.models.ResponseObject

class ConfigApiHandler : ObjectResponseHandler<ConfigDto>, ArrayResponseHandler<ConfigDto> {
    override fun onSuccess(collection: Array<ConfigDto>) {

    }

    override fun onError(error: String) {

    }

    override fun onSuccess(obj: ResponseObject<ConfigDto>) {
        val apiBaseUrl = obj.Data!!.baseUrl
        val sessionBaseUrl = Prefs.getString(Constants.CONFIG_BASE_URL_KEY, null)

        if (!sessionBaseUrl.equals(apiBaseUrl)){
            Prefs.putAny(Constants.CONFIG_BASE_URL_KEY, apiBaseUrl!!)
        }
    }
}
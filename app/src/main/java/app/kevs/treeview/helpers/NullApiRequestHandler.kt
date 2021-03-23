package app.kevs.treeview.helpers

import com.imu.flowerdelivery.network.callbacks.ArrayResponseHandler
import com.imu.flowerdelivery.network.callbacks.ObjectResponseHandler
import com.imu.flowerdelivery.network.models.ResponseObject

class NullApiRequestHandler<T> : ArrayResponseHandler<T>, ObjectResponseHandler<T>{
    override fun onSuccess(collection: Array<T>) {
        println("test")
    }

    override fun onError(error: String) {
        println("test")
    }

    override fun onSuccess(obj: ResponseObject<T>) {
        println("test")
    }

}
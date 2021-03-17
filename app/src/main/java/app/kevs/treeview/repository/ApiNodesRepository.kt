package app.kevs.treeview.repository

import android.content.Context
import app.kevs.treeview.StringNode
import app.kevs.treeview.network.models.NodeDto
import com.imu.flowerdelivery.network.ApiManager
import com.imu.flowerdelivery.network.callbacks.ArrayResponseHandler
import com.imu.flowerdelivery.network.callbacks.ObjectResponseHandler
import com.imu.flowerdelivery.network.models.ResponseObject

class ApiNodesRepository(ctx: Context, repo: InMemoryNodesRepository) : INodesRepository, ArrayResponseHandler<NodeDto>, ObjectResponseHandler<NodeDto>{
    var repo = repo
    val apiManager = ApiManager.getInstance(ctx)

    override fun removeNode(nodeName: String?) {
        TODO("Not yet implemented")
    }

    override fun addNode(nodeName: String?, parentNode: String?) {
        TODO("Not yet implemented")
    }

    override fun GetRoot(): StringNode {
        TODO("Not yet implemented")
    }

    override fun onSuccess(collection: Array<NodeDto>) {
        TODO("Not yet implemented")
    }

    override fun onError(error: String) {
        TODO("Not yet implemented")
    }

    override fun onSuccess(obj: ResponseObject<NodeDto>) {
        TODO("Not yet implemented")
    }

}
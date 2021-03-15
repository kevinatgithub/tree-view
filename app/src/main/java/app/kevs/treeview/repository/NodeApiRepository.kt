package app.kevs.treeview.repository

import android.content.Context
import app.kevs.treeview.network.models.Node
import com.imu.flowerdelivery.network.ApiManager
import com.imu.flowerdelivery.network.callbacks.ArrayResponseHandler
import com.imu.flowerdelivery.network.callbacks.ObjectResponseHandler

class NodeApiRepository(ctx: Context, project: String) : INodeDataRepository{
    val ctx = ctx
    val project = project
    val api = ApiManager.getInstance(ctx)

    override fun RemoveNode(
        nodeName: String,
        path: String,
        handler: ObjectResponseHandler<Node>
    ) {
        val node = Node(project, nodeName, path)
        api.deleteNode(node).enqueue(ApiManager.setDefaultHandler(handler))
    }

    override fun AddNode(nodeName: String, path: String, handler: ObjectResponseHandler<Node>) {
        val node = Node(project, nodeName, path)
        api.addNode(node).enqueue(ApiManager.setDefaultHandler(handler))
    }

    override fun GetNodes(project: String, handler: ArrayResponseHandler<Node>){
        api.getAllNodes(project).enqueue(ApiManager.setArrayDefaultHandler(handler))
    }

}
package app.kevs.treeview

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import app.kevs.treeview.network.models.NodeDto
import app.kevs.treeview.repository.*
import app.kevs.treeview.services.NodesServices
import com.bakhtiyor.gradients.Gradients
import com.cesarferreira.pluralize.singularize
import com.imu.flowerdelivery.network.callbacks.ArrayResponseHandler
import com.imu.flowerdelivery.network.callbacks.ObjectResponseHandler
import com.imu.flowerdelivery.network.models.ResponseObject
import de.blox.treeview.TreeView


class MainActivity : AppCompatActivity(), NodeOnClick, ArrayResponseHandler<NodeDto>,
    ObjectResponseHandler<NodeDto> {

    companion object{
        var user = ""
        var projectName = ""
        var nodeDtos: List<NodeDto>? = null
        var tempRootNodeDto: NodeDto? = null
    }

    var repo : NodeApiRepository? = null
    var tvHelper : TreeViewHelper? = null
    var zoomOutNodeButton : ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION and View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        supportActionBar?.hide()

        findViewById<ConstraintLayout>(R.id.container).background = Gradients.premiumDark()

        user = intent.getStringExtra("user").toString()
        projectName = intent.getStringExtra("project").toString()

        findViewById<ImageView>(R.id.btnZoom).setOnClickListener {
            val i = Intent(this, ZoomActivity::class.java)
            startActivity(i)
        }

        zoomOutNodeButton = findViewById<ImageView>(R.id.btnZoomOut)
        zoomOutNodeButton!!.setOnClickListener {
            tempRootNodeDto = null
            repo!!.GetNodes(projectName, this)
            toggleZoomNodeButton()
        }

        repo = NodeApiRepository(this, projectName)
        repo!!.GetNodes(projectName, this)
    }

    override fun onResume() {
        super.onResume()
        toggleZoomNodeButton()
    }

    private fun toggleZoomNodeButton(){
        if (tempRootNodeDto == null)
            zoomOutNodeButton!!.visibility = View.GONE
        else
            zoomOutNodeButton!!.visibility = View.VISIBLE
    }

    override fun onClick(data: Object, position: Int) {
        val parentNode = data as NodeDto

        val callback1 = NodesServices.PromptNodeNameCallback { nodeName: String, externalProjectName : String, type: String, refType: String ->
            val path = parentNode.Path+"."+parentNode.NodeName
            when (type){
                Constants.NODE_TYPE_MVC -> {
                    val childPath = path + Constants.PATH_DELIMITER + nodeName
                    repo!!.AddNode("Controllers", childPath, this)
                    repo!!.AddNode("Models", childPath, this)
                    repo!!.AddNode("Views", childPath, this)
                    repo!!.AddNode(nodeName, path, this)
                }
                Constants.NODE_TYPE_SUB_PROJECT -> {
                    val childPath = path + Constants.PATH_DELIMITER + nodeName
                    repo!!.AddNode("Implementations", childPath, this)
                    repo!!.AddNode("Interfaces", childPath, this)
                    repo!!.AddNode(nodeName, path, this)
                }
                Constants.NODE_TYPE_INTERFACE -> {
                    val parentPathArray = parentNode.Path!!.split(Constants.PATH_DELIMITER)
                    val newNodeName = "I" + nodeName + parentPathArray[parentPathArray.size-1].singularize()
                    val childPath = path + Constants.PATH_DELIMITER + newNodeName
                    repo!!.AddNode("Methods", childPath, this)
                    repo!!.AddNode("Dependencies", childPath, this)
                    repo!!.AddNode(newNodeName, path, this)
                }
                Constants.NODE_TYPE_CONTROLLER -> {
                    val newNodeName = nodeName + "Controller"
                    val childPath = path + Constants.PATH_DELIMITER + newNodeName
                    repo!!.AddNode("Endpoints", childPath, this)
                    repo!!.AddNode("Dependencies", childPath, this)
                    repo!!.AddNode(newNodeName, path, this)
                }
                else -> {
                    repo!!.AddNode(nodeName, path, this)
                }
            }
        }

        var callback2 = NodesServices.NodeZoomCallback {
            tempRootNodeDto = parentNode
            //repo!!.GetNodes(projectName, this)
            tvHelper!!.renderTree()
            toggleZoomNodeButton()
        }

        NodesServices.PromptNodeName(this, callback1, callback2)
    }

    override fun onLongClick(data: Object, position: Int): Boolean {
        val parentNode = data as NodeDto
        val callback2 = NodesServices.NodeDeleteCallback{
            repo!!.RemoveNode(parentNode.NodeName!!, parentNode.Path!!, this)
        }
        NodesServices.ConfirmNodeDeletion(this, callback2)
        return true
    }

    override fun onSuccess(collection: Array<NodeDto>) {
        if (collection.size == 0){
            var projectNameArray = projectName.split(Constants.PROJECT_DELIMITER)
            repo!!.AddNode(projectNameArray.last(), projectName, this)
            return
        }
        nodeDtos = collection.asList()

        if (nodeDtos == null)
            return

        val treeView : TreeView = findViewById(R.id.idTreeView)

        tvHelper = TreeViewHelper(this, treeView, this, nodeDtos!!)
        treeView.visibility = View.VISIBLE
        tvHelper!!.renderTree()
    }

    override fun onError(error: String) {
        Toast.makeText(this, "$error", Toast.LENGTH_SHORT).show()
    }

    override fun onSuccess(obj: ResponseObject<NodeDto>) {
        repo!!.GetNodes(projectName, this)
    }
}
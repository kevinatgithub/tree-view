package app.kevs.treeview

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import app.kevs.treeview.Constants.Companion.NODE_TYPE_ADD_NEW_PROJECT_REFERENCE
import app.kevs.treeview.Constants.Companion.NODE_TYPE_ADD_REFERENCE
import app.kevs.treeview.Constants.Companion.NODE_TYPE_CONTAINER_CONTROLLER
import app.kevs.treeview.Constants.Companion.NODE_TYPE_CONTAINER_DEPENDENCIES
import app.kevs.treeview.Constants.Companion.NODE_TYPE_CONTAINER_ENDPOINTS
import app.kevs.treeview.Constants.Companion.NODE_TYPE_CONTAINER_IMPLEMENTATION
import app.kevs.treeview.Constants.Companion.NODE_TYPE_CONTAINER_INTERFACES
import app.kevs.treeview.Constants.Companion.NODE_TYPE_CONTAINER_METHODS
import app.kevs.treeview.Constants.Companion.NODE_TYPE_CONTAINER_MODEL
import app.kevs.treeview.Constants.Companion.NODE_TYPE_CONTAINER_STRATEGY
import app.kevs.treeview.Constants.Companion.NODE_TYPE_CONTAINER_VIEW
import app.kevs.treeview.Constants.Companion.NODE_TYPE_CONTROLLER
import app.kevs.treeview.Constants.Companion.NODE_TYPE_CRUD
import app.kevs.treeview.Constants.Companion.NODE_TYPE_IMPLEMENTATION
import app.kevs.treeview.Constants.Companion.NODE_TYPE_INTERFACE
import app.kevs.treeview.Constants.Companion.NODE_TYPE_LINK
import app.kevs.treeview.Constants.Companion.NODE_TYPE_MODEL
import app.kevs.treeview.Constants.Companion.NODE_TYPE_MVC
import app.kevs.treeview.Constants.Companion.NODE_TYPE_ROOT
import app.kevs.treeview.Constants.Companion.NODE_TYPE_SUB_PROJECT
import app.kevs.treeview.Constants.Companion.NODE_TYPE_VIEW
import app.kevs.treeview.Constants.Companion.PATH_DELIMITER
import app.kevs.treeview.Constants.Companion.PROJECT_DELIMITER
import app.kevs.treeview.Constants.Companion.REFERENCE_TYPE_CONTROLLER
import app.kevs.treeview.Constants.Companion.REFERENCE_TYPE_MODEL
import app.kevs.treeview.Constants.Companion.REFERENCE_TYPE_MVC
import app.kevs.treeview.Constants.Companion.REFERENCE_TYPE_STRATEGY
import app.kevs.treeview.helpers.DependencySelector
import app.kevs.treeview.helpers.NodeOnClick
import app.kevs.treeview.helpers.NodesServices
import app.kevs.treeview.helpers.NullApiRequestHandler
import app.kevs.treeview.network.models.NodeDto
import app.kevs.treeview.network.models.NodeUpdateDto
import com.bakhtiyor.gradients.Gradients
import com.cesarferreira.pluralize.pluralize
import com.cesarferreira.pluralize.singularize
import com.imu.flowerdelivery.network.ApiManager
import com.imu.flowerdelivery.network.ApiManager.Companion.setArrayDefaultHandler
import com.imu.flowerdelivery.network.ApiManager.Companion.setDefaultHandler
import com.imu.flowerdelivery.network.callbacks.ArrayResponseHandler
import com.imu.flowerdelivery.network.callbacks.ObjectResponseHandler
import com.imu.flowerdelivery.network.interfaces.TreeApi
import com.imu.flowerdelivery.network.models.ResponseObject
import de.blox.graphview.Graph
import de.blox.graphview.GraphAdapter
import de.blox.graphview.GraphView
import de.blox.graphview.Node
import de.blox.graphview.energy.FruchtermanReingoldAlgorithm
import de.blox.graphview.layered.SugiyamaAlgorithm
import de.blox.graphview.layered.SugiyamaConfiguration
import de.blox.graphview.tree.BuchheimWalkerAlgorithm
import de.blox.graphview.tree.BuchheimWalkerConfiguration
import java.util.*
import kotlin.collections.ArrayList


class GraphViewActivity : AppCompatActivity(), NodeOnClick, ArrayResponseHandler<NodeDto>,
    ObjectResponseHandler<NodeDto> {

    private var graphType = 1
    private var nodesData = ArrayList<NodeDto>()
    private var user = ""
    var projectName = ""
    var nodes = ArrayList<Node>()
    var api : TreeApi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_graph_view)

        api = ApiManager.getInstance(this)

        supportActionBar?.hide()

        user = intent.getStringExtra("user").toString()
        projectName = intent.getStringExtra("project").toString()

        findViewById<ConstraintLayout>(R.id.container).background = Gradients.premiumDark()
        findViewById<ImageView>(R.id.btnChangeGraphType).setOnClickListener {
            changeGraphType()
        }
        api!!.getAllNodes(projectName).enqueue(setArrayDefaultHandler(this))
    }

    private fun changeGraphType() {
        when(graphType){
            1 -> {
                graphType = 2
                parseNodesData()
            }
            2 -> {
                graphType = 1
                parseNodesData()
            }
            3 -> {
                graphType = 1
                parseNodesData()
            }
        }
    }

    private fun parseNodesData() {
        val graphView = findViewById<GraphView>(R.id.graph)
        graphView.visibility = View.VISIBLE

        val graph = Graph()

        nodes = ArrayList()
        nodesData.forEach {
            val node = Node(it)
            nodes.add(node)
            if (it.Path.equals(projectName))
                graph.addNode(node)
        }

        nodes.forEach {
            val childNode = it.data as NodeDto
            if (!childNode.Path.equals(projectName)){

                val parentNode = nodes.find { n ->
                    val parentNode = n.data as NodeDto
                    var pathArray = childNode.Path!!.split(PATH_DELIMITER)
                    val parentName = pathArray[pathArray.size - 1]
                    pathArray = pathArray.subList(0, pathArray.size - 1)
                    val newPath = pathArray.joinToString(PATH_DELIMITER)
                    parentNode.Path.equals(newPath) && parentNode.NodeName.equals(parentName) && parentNode.ProjectName.equals(childNode.ProjectName)
                }

                if (parentNode != null)
                    graph.addEdge(it, parentNode)
            }
        }

        nodes.forEach {
            val owner = it
            val childNode = it.data as NodeDto
            if (childNode.Links != null) {
                childNode.Links!!.forEach { it1 ->
                    val linkedNode = nodes.find { it2 ->
                        val toLinkNodeData = it2.data as NodeDto
                        toLinkNodeData.NodeName.equals(it1.NodeName) && toLinkNodeData.Path.equals(
                            it1.Path)
                    }
                    if (linkedNode != null)
                        graph.addEdge(owner, linkedNode)
                }
            }
        }

        val adapter = object : GraphAdapter<GraphView.ViewHolder>(graph) {
            override fun getCount(): Int = nodes.size

            override fun getItem(position: Int): Any = nodes[position]

            override fun isEmpty(): Boolean = nodes.any()

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GraphView.ViewHolder = SimpleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.node, parent, false))

            override fun onBindViewHolder(viewHolder: GraphView.ViewHolder, data: Any, position: Int) {
                val node = data as Node
                val nodeDto = node.data as NodeDto
                val holder = viewHolder as SimpleViewHolder
                if (nodeDto.Path.equals(projectName))
                    holder.textView.setBackgroundColor(ContextCompat.getColor(this@GraphViewActivity, android.R.color.holo_orange_dark))
                else if(nodeDto.NodeName!!.toUpperCase(Locale.US).contains("[REF]")){
                    holder.textView.setBackgroundColor(ContextCompat.getColor(this@GraphViewActivity, android.R.color.holo_orange_dark))
                    holder.textView.setOnClickListener {
                        val i = Intent(this@GraphViewActivity, GraphViewActivity::class.java)
                        i.putExtra("user",user)
                        val newProjectName = ProjectsActivity.user + PROJECT_DELIMITER + nodeDto.NodeName!!.replace("[REF] ","")
                        i.putExtra("project", newProjectName)
                        this@GraphViewActivity.startActivity(i)
                    }
                    val dataLocal = node.data as NodeDto
                    holder.textView.setOnLongClickListener { this@GraphViewActivity.onRefLongClick(dataLocal) }
                }else if(nodeDto.NodeName!!.toUpperCase(Locale.US).contains("\n")){
                    holder.textView.setBackgroundColor(Color.parseColor("#995FA3"))
                }else if(nodeDto.NodeName!!.toUpperCase(Locale.US).contains("DEPENDENCY")){
                    holder.textView.setBackgroundColor(Color.parseColor("#FE5F55"))
                }else if(nodeDto.NodeName!!.toUpperCase(Locale.US).contains("REPOSITORY") || nodeDto.NodeName!!.toUpperCase(Locale.US).contains("REPOSITORIES")) {
                    holder.textView.setBackgroundColor(Color.parseColor("#adf7b6"))
                    holder.textView.setTextColor(Color.parseColor("#000000"))
                }else if(nodeDto.NodeName!!.toUpperCase(Locale.US).contains("SERVICE")) {
                    holder.textView.setBackgroundColor(Color.parseColor("#ffee93"))
                    holder.textView.setTextColor(Color.parseColor("#000000"))
                }else if(nodeDto.NodeName!!.toUpperCase(Locale.US).contains("CONTROLLER")){
                    holder.textView.setBackgroundColor(Color.parseColor("#224870"))
                }else if(nodeDto.NodeName!!.toUpperCase(Locale.US).contains("MODEL")){
                    holder.textView.setBackgroundColor(Color.parseColor("#fcf5c7"))
                    holder.textView.setTextColor(Color.parseColor("#000000"))
                }else if(nodeDto.NodeName!!.toUpperCase(Locale.US).contains("VIEW")){
                    holder.textView.setBackgroundColor(Color.parseColor("#4EA5D9"))
                    holder.textView.setTextColor(Color.parseColor("#000000"))
                }
                else
                    holder.textView.setBackgroundColor(Color.parseColor("#79addc"))

                holder.textView.text = nodeDto.NodeName
                if (!nodeDto.NodeName!!.contains("[REF]")){
                    holder.textView.setOnClickListener { this@GraphViewActivity.onClick(node.data, position) }
                    holder.textView.setOnLongClickListener { this@GraphViewActivity.onLongClick(node.data, position) }
                }
            }
        }

        graphView.adapter = adapter

        val graphType3 = BuchheimWalkerConfiguration.Builder()
            .setSiblingSeparation(100)
            .setLevelSeparation(300)
            .setSubtreeSeparation(300)
            .setOrientation(BuchheimWalkerConfiguration.ORIENTATION_TOP_BOTTOM)
            .build()
        val graphType2 = FruchtermanReingoldAlgorithm()
        val graphType1 = SugiyamaConfiguration.Builder()
            .setNodeSeparation(200)
            .setLevelSeparation(200)
            .build()
        when(graphType){
            1 -> graphView.setLayout(SugiyamaAlgorithm(graphType1))
            2 -> graphView.setLayout(graphType2)
            3 -> graphView.setLayout(BuchheimWalkerAlgorithm(graphType3))
        }
    }

    private fun onRefLongClick(data: NodeDto): Boolean {
        NodesServices.ConfirmRefNodeDeletion(this, {
            api!!.deleteNode(NodeDto(projectName, data.NodeName, data.Path)).enqueue(setDefaultHandler(this@GraphViewActivity))
            api!!.deleteNodesInPath(data.Path!!).enqueue(setArrayDefaultHandler(NullApiRequestHandler()))
        }, {
            DependencySelector(this, data, nodesData,
                NodesServices.DependencySelectedHandler { selected, _ ->
                    val copy = data.Clone()
                    copy.AddLink(selected!!)
                    api!!.updateNode(NodeUpdateDto(data, copy)).enqueue(setDefaultHandler(this@GraphViewActivity))
                }).showDependencySelector()
        })

        return true
    }

    internal class SimpleViewHolder(itemView: View) :
        GraphView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById(R.id.text)
    }

    override fun onClick(data: Any, position: Int) {
        val parentNode = data as NodeDto

        val callback1 = NodesServices.PromptNodeNameCallback { nodeName: String, externalProjectName : String, type: String, refType: String ->
            val path = parentNode.Path+"."+parentNode.NodeName
            val nullHandler = NullApiRequestHandler<NodeDto>()
            when (type){
                NODE_TYPE_MVC -> {
                    val childPath = path + PATH_DELIMITER + nodeName
                    api!!.addNode(NodeDto(projectName, "Controllers", childPath, NODE_TYPE_CONTAINER_CONTROLLER)).enqueue(setDefaultHandler(nullHandler))
                    api!!.addNode(NodeDto(projectName, "Models", childPath, NODE_TYPE_CONTAINER_MODEL)).enqueue(setDefaultHandler(nullHandler))
                    api!!.addNode(NodeDto(projectName, "Views", childPath, NODE_TYPE_CONTAINER_VIEW)).enqueue(setDefaultHandler(nullHandler))
                    api!!.addNode(NodeDto(projectName, nodeName, path, NODE_TYPE_MVC)).enqueue(setDefaultHandler(this))
                }
                NODE_TYPE_SUB_PROJECT -> {
                    val childPath = path + PATH_DELIMITER + nodeName
                    api!!.addNode(NodeDto(projectName, "Implementations", childPath, NODE_TYPE_CONTAINER_IMPLEMENTATION)).enqueue(setDefaultHandler(nullHandler))
                    api!!.addNode(NodeDto(projectName, "Interfaces", childPath, NODE_TYPE_CONTAINER_INTERFACES)).enqueue(setDefaultHandler(nullHandler))
                    api!!.addNode(NodeDto(projectName, nodeName, path, NODE_TYPE_SUB_PROJECT)).enqueue(setDefaultHandler(this))
                }
                NODE_TYPE_INTERFACE -> {
                    val parentPathArray = parentNode.Path!!.split(PATH_DELIMITER)
                    val newNodeName = "I" + nodeName + parentPathArray[parentPathArray.size-1].singularize()
                    val childPath = path + PATH_DELIMITER + newNodeName
                    api!!.addNode(NodeDto(projectName, "Methods", childPath, NODE_TYPE_CONTAINER_METHODS)).enqueue(setDefaultHandler(nullHandler))
                    api!!.addNode(NodeDto(projectName, "Dependencies", childPath, NODE_TYPE_CONTAINER_DEPENDENCIES)).enqueue(setDefaultHandler(nullHandler))
                    api!!.addNode(NodeDto(projectName, newNodeName, path, NODE_TYPE_INTERFACE)).enqueue(setDefaultHandler(this))
                }
                NODE_TYPE_CONTROLLER -> {
                    val newNodeName = nodeName + "Controller"
                    val childPath = path + PATH_DELIMITER + newNodeName
                    api!!.addNode(NodeDto(projectName, "Endpoints", childPath, NODE_TYPE_CONTAINER_ENDPOINTS)).enqueue(setDefaultHandler(nullHandler))
                    api!!.addNode(NodeDto(projectName, "Dependencies", childPath, NODE_TYPE_CONTAINER_DEPENDENCIES)).enqueue(setDefaultHandler(nullHandler))
                    api!!.addNode(NodeDto(projectName, newNodeName, path, NODE_TYPE_CONTROLLER)).enqueue(setDefaultHandler(this))
                }
                NODE_TYPE_MODEL -> {
                    val newNodeName = nodeName + "Model"
                    api!!.addNode(NodeDto(projectName, newNodeName, path, NODE_TYPE_MODEL)).enqueue(setDefaultHandler(this))
                }
                NODE_TYPE_VIEW -> {
                    val newNodeName = nodeName + "View"
                    api!!.addNode(NodeDto(projectName, newNodeName, path, NODE_TYPE_VIEW)).enqueue(setDefaultHandler(this))
                }
                NODE_TYPE_CRUD -> {
                    val newNodeName = "GetAll${nodeName.pluralize()}\nGet${nodeName}ById\nCreate$nodeName\nUpdate$nodeName\nDelete$nodeName"
                    api!!.addNode(NodeDto(projectName, newNodeName, path, NODE_TYPE_CRUD)).enqueue(setDefaultHandler(this))
                }
                NODE_TYPE_ADD_REFERENCE, NODE_TYPE_ADD_NEW_PROJECT_REFERENCE ->{
                    when (refType){
                        REFERENCE_TYPE_MVC->{
                            val newNodeName = "[REF] "  + if (nodeName.isEmpty()) externalProjectName else nodeName
                            val internalProjectName = ProjectsActivity.user + PROJECT_DELIMITER + nodeName
                            val childPath = internalProjectName + PATH_DELIMITER + nodeName
                            api!!.addNode(NodeDto(internalProjectName, "Models", childPath, NODE_TYPE_CONTAINER_MODEL)).enqueue(setDefaultHandler(nullHandler))
                            api!!.addNode(NodeDto(internalProjectName, "Views", childPath, NODE_TYPE_CONTAINER_VIEW)).enqueue(setDefaultHandler(nullHandler))
                            api!!.addNode(NodeDto(internalProjectName, "Controllers", childPath, NODE_TYPE_CONTAINER_CONTROLLER)).enqueue(setDefaultHandler(nullHandler))
                            api!!.addNode(NodeDto(internalProjectName, nodeName, internalProjectName, NODE_TYPE_CONTAINER_STRATEGY)).enqueue(setDefaultHandler(nullHandler))
                            api!!.addNode(NodeDto(projectName, newNodeName, path, NODE_TYPE_ADD_REFERENCE)).enqueue(setDefaultHandler(this))
                        }
                        REFERENCE_TYPE_STRATEGY -> {
                            val newNodeName = "[REF] "  + if (nodeName.isEmpty()) externalProjectName else nodeName
                            val internalProjectName = ProjectsActivity.user + PROJECT_DELIMITER + nodeName
                            val childPath = internalProjectName + PATH_DELIMITER + nodeName
                            api!!.addNode(NodeDto(internalProjectName, "Interfaces", childPath, NODE_TYPE_CONTAINER_INTERFACES)).enqueue(setDefaultHandler(nullHandler))
                            api!!.addNode(NodeDto(internalProjectName, "Implementation", childPath, NODE_TYPE_CONTAINER_IMPLEMENTATION)).enqueue(setDefaultHandler(nullHandler))
                            api!!.addNode(NodeDto(internalProjectName, nodeName, internalProjectName, NODE_TYPE_CONTAINER_STRATEGY)).enqueue(setDefaultHandler(nullHandler))
                            api!!.addNode(NodeDto(projectName, newNodeName, path, NODE_TYPE_ADD_REFERENCE)).enqueue(setDefaultHandler(this))

                        }
                        REFERENCE_TYPE_CONTROLLER->{
                            val newNodeName = "[REF] "  + if (nodeName.isEmpty()) externalProjectName else nodeName + "Controller"
                            val internalProjectName = ProjectsActivity.user + PROJECT_DELIMITER + nodeName + "Controller"
                            val childPath = internalProjectName + PATH_DELIMITER + nodeName + "Controller"
                            api!!.addNode(NodeDto(internalProjectName, "Endpoints", childPath, NODE_TYPE_CONTAINER_ENDPOINTS)).enqueue(setDefaultHandler(nullHandler))
                            api!!.addNode(NodeDto(internalProjectName, "Dependencies", childPath, NODE_TYPE_CONTAINER_DEPENDENCIES)).enqueue(setDefaultHandler(nullHandler))
                            api!!.addNode(NodeDto(internalProjectName, nodeName + "Controller", internalProjectName, NODE_TYPE_CONTAINER_STRATEGY)).enqueue(setDefaultHandler(nullHandler))
                            api!!.addNode(NodeDto(projectName, newNodeName, path, NODE_TYPE_ADD_REFERENCE)).enqueue(setDefaultHandler(this))
                        }
                        REFERENCE_TYPE_MODEL ->{
                            val newNodeName = "[REF] "  + if (nodeName.isEmpty()) externalProjectName else nodeName
                            val internalProjectName = ProjectsActivity.user + PROJECT_DELIMITER + nodeName
                            api!!.addNode(NodeDto(internalProjectName, nodeName, internalProjectName, NODE_TYPE_MODEL)).enqueue(setDefaultHandler(nullHandler))
                            api!!.addNode(NodeDto(projectName, newNodeName, path, NODE_TYPE_ADD_REFERENCE)).enqueue(setDefaultHandler(this))
                        }
                        else -> {
                            val newNodeName = "[REF] "  + if (nodeName.isEmpty()) externalProjectName else nodeName
                            api!!.addNode(NodeDto(projectName, newNodeName, path, NODE_TYPE_ADD_REFERENCE)).enqueue(setDefaultHandler(this))
                        }
                    }
                }
                NODE_TYPE_LINK -> {
                    val callback = NodesServices.DependencySelectedHandler { selected, _ ->
                        val copy = parentNode.Clone()
                        copy.AddLink(selected)
                        api!!.updateNode(NodeUpdateDto(parentNode, copy)).enqueue(setDefaultHandler(this))
                    }
                    DependencySelector(this, parentNode, nodesData, callback).showLinkToNodeSelector()
                }
                else -> api!!.addNode(NodeDto(projectName, nodeName, path)).enqueue(setDefaultHandler(this))
            }
        }

        val callback2 = NodesServices.NodeNeutralCallback {
            onNeutral(it, parentNode)
        }

        NodesServices.PromptNodeName(this, parentNode.Type, callback1, callback2)
    }

    private fun onNeutral(name: String, node: NodeDto) {
        val nodePathArray = node.Path!!.split(PATH_DELIMITER)
        val newPath = ArrayList(nodePathArray.dropLast(1))
        when(node.Type){
            NODE_TYPE_INTERFACE -> {
                api!!.addNode(NodeDto(projectName, "Implementation",newPath.joinToString(PATH_DELIMITER), NODE_TYPE_CONTAINER_IMPLEMENTATION) ).enqueue(setDefaultHandler(this))
                newPath.add("Implementation")
                val implementationName = name + node.NodeName!!.substring(1, node.NodeName!!.length)
                api!!.addNode(NodeDto(projectName, implementationName,newPath.joinToString(PATH_DELIMITER), NODE_TYPE_IMPLEMENTATION) ).enqueue(setDefaultHandler(this))
            }
            NODE_TYPE_CONTAINER_DEPENDENCIES -> {
                val callback = object : ArrayResponseHandler<NodeDto> {
                    override fun onSuccess(collection: Array<NodeDto>) {
                        val nodesDataLocal = collection.toCollection(ArrayList())
                        DependencySelector(this@GraphViewActivity, node, nodesDataLocal, NodesServices.DependencySelectedHandler {
                                selected, _ -> api!!.addNode(NodeDto(projectName, "[REF] " +selected.NodeName, node.Path + PATH_DELIMITER + node.NodeName, NODE_TYPE_ADD_REFERENCE)).enqueue(setDefaultHandler(this@GraphViewActivity))
                        }).showDependencySelector()
                    }

                    override fun onError(error: String) {
                        Toast.makeText(this@GraphViewActivity, error, Toast.LENGTH_LONG).show()
                    }

                }
                api!!.getAllNodesByUser(user).enqueue(setArrayDefaultHandler(callback))

            }
        }
    }

    override fun onLongClick(data: Any, position: Int): Boolean {
        val parentNode = data as NodeDto
        val deleteCallback = NodesServices.NodeDeleteCallback{
            api!!.deleteNode(NodeDto(projectName, parentNode.NodeName, parentNode.Path)).enqueue(setDefaultHandler(this@GraphViewActivity))
            api!!.deleteNodesInPath(parentNode.Path!!).enqueue(setArrayDefaultHandler(NullApiRequestHandler()))
        }

        val updateCallback = NodesServices.NodeUpdateCallback {
            NodesServices.Rename(this) {
                val name = it.toString()
                val changes = parentNode.Clone()
                changes.NodeName = name
                api!!.updateNode(NodeUpdateDto(parentNode, changes)).enqueue(setDefaultHandler(this))
            }
        }

        NodesServices.ConfirmNodeDeletion(this, deleteCallback, updateCallback)
        return true
    }

    override fun onSuccess(collection: Array<NodeDto>) {
        if (collection.isEmpty()){
            val projectNameArray = projectName.split(PROJECT_DELIMITER)
            api!!.addNode(NodeDto(projectName, projectNameArray.last(), projectName, NODE_TYPE_ROOT)).enqueue(setDefaultHandler(this))
            return
        }
        nodesData = collection.toCollection(ArrayList())

        parseNodesData()
    }

    override fun onError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    override fun onSuccess(obj: ResponseObject<NodeDto>) {
        graphType = 1
        api!!.getAllNodes(projectName).enqueue(setArrayDefaultHandler(this))
    }
}
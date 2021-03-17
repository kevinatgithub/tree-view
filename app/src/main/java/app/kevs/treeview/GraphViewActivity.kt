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
import app.kevs.treeview.Constants.Companion.NODE_TYPE_ADD_REFERENCE
import app.kevs.treeview.Constants.Companion.PATH_DELIMITER
import app.kevs.treeview.Constants.Companion.PROJECT_DELIMITER
import app.kevs.treeview.Constants.Companion.REFERENCE_TYPE_Strategy
import app.kevs.treeview.network.models.NodeDto
import app.kevs.treeview.network.models.Project
import app.kevs.treeview.repository.NodeApiRepository
import app.kevs.treeview.services.NodesServices
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


class GraphViewActivity : AppCompatActivity(), NodeOnClick, ArrayResponseHandler<NodeDto>,
    ObjectResponseHandler<NodeDto> {

    private var graphType = 1
    private var nodesData = ArrayList<NodeDto>()
    private var user = ""
    var projectName = ""
    var nodes = ArrayList<Node>()
    private var repo : NodeApiRepository? = null
    var api : TreeApi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_graph_view)

        api = ApiManager.getInstance(this)

        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION and View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        supportActionBar?.hide()

        user = intent.getStringExtra("user").toString()
        projectName = intent.getStringExtra("project").toString()

        findViewById<ConstraintLayout>(R.id.container).background = Gradients.premiumDark()
        findViewById<ImageView>(R.id.btnChangeGraphType).setOnClickListener {
            changeGraphType()
        }

        repo = NodeApiRepository(this, projectName)
        repo!!.GetNodes(projectName, this)
    }

    private fun changeGraphType() {
        when(graphType){
            1 -> {
                parseNodesData()
                graphType = 2
            }
            2 -> {
                parseNodesData()
                graphType = 1
            }
            3 -> {
                parseNodesData()
                graphType = 1
            }
        }
    }

    private fun parseNodesData() {
        val graphView = findViewById<GraphView>(R.id.graph)
        graphView.visibility = View.VISIBLE

        val graph = Graph()

        nodes = ArrayList<Node>()
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
                    var pathArray = childNode.Path!!.split(Constants.PATH_DELIMITER)
                    var parentName = pathArray.get(pathArray.size - 1)
                    pathArray = pathArray.subList(0, pathArray.size - 1)
                    val newPath = pathArray.joinToString(Constants.PATH_DELIMITER)
                    parentNode.Path.equals(newPath) && parentNode.NodeName.equals(parentName) && parentNode.ProjectName.equals(childNode.ProjectName)
                }

                if (parentNode != null)
                    graph.addEdge(it, parentNode)
            }
        }

        var adapter = object : GraphAdapter<GraphView.ViewHolder>(graph) {
            override fun getCount(): Int = nodes.size

            override fun getItem(position: Int): Any = nodes.get(position)

            override fun isEmpty(): Boolean = nodes.any()

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GraphView.ViewHolder = SimpleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.node, parent, false))

            override fun onBindViewHolder(viewHolder: GraphView.ViewHolder, data: Any, position: Int) {
                val node = data as Node
                val nodeDto = node.data as NodeDto
                val holder = viewHolder as SimpleViewHolder
                if (nodeDto.Path.equals(projectName))
                    holder.textView.setBackgroundColor(resources.getColor(android.R.color.holo_orange_dark))
                else if(nodeDto.NodeName!!.toUpperCase().contains("[REF]")){
                    holder.textView.setBackgroundColor(resources.getColor(android.R.color.holo_orange_dark))
                    holder.textView.setOnClickListener {
                        val i = Intent(this@GraphViewActivity, GraphViewActivity::class.java)
                        i.putExtra("user",user)
                        val newProjectName = ProjectsActivity.user + Constants.PROJECT_DELIMITER + nodeDto.NodeName!!.replace("[REF] ","")
                        i.putExtra("project", newProjectName)
                        this@GraphViewActivity.startActivity(i)
                    }
                }else if(nodeDto.NodeName!!.toUpperCase().contains("\n")){
                    holder.textView.setBackgroundColor(Color.parseColor("#995FA3"))
                }else if(nodeDto.NodeName!!.toUpperCase().contains("DEPENDENCY")){
                    holder.textView.setBackgroundColor(Color.parseColor("#FE5F55"))
                }else if(nodeDto.NodeName!!.toUpperCase().contains("REPOSITORY") || nodeDto.NodeName!!.toUpperCase().contains("REPOSITORIES")) {
                    holder.textView.setBackgroundColor(Color.parseColor("#adf7b6"))
                    holder.textView.setTextColor(Color.parseColor("#000000"))
                }else if(nodeDto.NodeName!!.toUpperCase().contains("SERVICE")) {
                    holder.textView.setBackgroundColor(Color.parseColor("#ffee93"))
                    holder.textView.setTextColor(Color.parseColor("#000000"))
                }else if(nodeDto.NodeName!!.toUpperCase().contains("CONTROLLER")){
                    holder.textView.setBackgroundColor(Color.parseColor("#224870"))
                }else if(nodeDto.NodeName!!.toUpperCase().contains("MODEL")){
                    holder.textView.setBackgroundColor(Color.parseColor("#fcf5c7"))
                    holder.textView.setTextColor(Color.parseColor("#000000"))
                }else if(nodeDto.NodeName!!.toUpperCase().contains("VIEW")){
                    holder.textView.setBackgroundColor(Color.parseColor("#4EA5D9"))
                    holder.textView.setTextColor(Color.parseColor("#000000"))
                }
                else
                    holder.textView.setBackgroundColor(Color.parseColor("#79addc"))

                holder.textView.setText(nodeDto.NodeName!!)
                if (!nodeDto.NodeName!!.contains("[REF]")){
                    holder.textView.setOnClickListener { this@GraphViewActivity.onClick(node.data as Object, position) }
                }
                holder.textView.setOnLongClickListener { this@GraphViewActivity.onLongClick(node.data as Object, position) }
            }
        }

        graphView!!.setAdapter(adapter);

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
            1 -> {
                graphView!!.setLayout(SugiyamaAlgorithm(graphType1))
            }
            2 -> {
                graphView!!.setLayout(graphType2)
            }
            3 -> {
                graphView!!.setLayout(BuchheimWalkerAlgorithm(graphType3))
            }
        }
    }

    internal class SimpleViewHolder(itemView: View) :
        GraphView.ViewHolder(itemView) {
        var textView: TextView

        init {
            textView = itemView.findViewById(R.id.text)
        }
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
                Constants.NODE_TYPE_IMPLEMENTATION -> {
                    val parentPathArray = parentNode.Path!!.split(Constants.PATH_DELIMITER)
                    val newNodeName = nodeName + parentPathArray[parentPathArray.size-1].singularize()
                    val childPath = path + Constants.PATH_DELIMITER + newNodeName
                    repo!!.AddNode(newNodeName, path, this)
                }
                Constants.NODE_TYPE_CONTROLLER -> {
                    val newNodeName = nodeName + "Controller"
                    val childPath = path + Constants.PATH_DELIMITER + newNodeName
                    repo!!.AddNode("Endpoints", childPath, this)
                    repo!!.AddNode("Dependencies", childPath, this)
                    repo!!.AddNode(newNodeName, path, this)
                }
                Constants.NODE_TYPE_MODEL -> {
                    val newNodeName = nodeName + "Model"
                    repo!!.AddNode(newNodeName, path, this)
                }
                Constants.NODE_TYPE_VIEW -> {
                    val newNodeName = nodeName + "View"
                    repo!!.AddNode(newNodeName, path, this)
                }
                Constants.NODE_TYPE_CRUD -> {
                    val nodeName = "GetAll${nodeName.pluralize()}\nGet${nodeName}ById\nCreate$nodeName\nUpdate$nodeName\nDelete$nodeName"
                    repo!!.AddNode(nodeName, path, this)
                }
                Constants.NODE_TYPE_DEPENDENCY_MODEL -> {
                    val newNodeName = nodeName + "ModelDependency"
                    repo!!.AddNode(newNodeName, path, this)
                }
                Constants.NODE_TYPE_DEPENDENCY_REPOSITORY -> {
                    val newNodeName = nodeName + "RepositoryDependency"
                    repo!!.AddNode(newNodeName, path, this)
                }
                Constants.NODE_TYPE_DEPENDENCY_SERVICE ->{
                    val newNodeName = nodeName + "ServiceDependency"
                    repo!!.AddNode(newNodeName, path, this)
                }
                NODE_TYPE_ADD_REFERENCE ->{
                    when (refType){
                        REFERENCE_TYPE_Strategy -> {
                            var newNodeName = "[REF] "  + if (nodeName.isEmpty()) externalProjectName else nodeName
                            val childPath = path + Constants.PATH_DELIMITER + nodeName

                            repo!!.AddNode(newNodeName, path, this)

                            val internalProjectName = ProjectsActivity.user + PROJECT_DELIMITER + nodeName

                            //api!!.addProject(Project(internalProjectName, user, refType)).enqueue(ApiManager.setDefaultHandler(NullApiRequestHandler()))
                            api!!.addNode(NodeDto(internalProjectName, nodeName, internalProjectName)).enqueue(setDefaultHandler(NullApiRequestHandler()))
                            api!!.addNode(NodeDto(internalProjectName, "Interfaces", internalProjectName + PATH_DELIMITER + nodeName)).enqueue(setDefaultHandler(NullApiRequestHandler()))
                            api!!.addNode(NodeDto(internalProjectName, "Implementation", internalProjectName + PATH_DELIMITER + nodeName)).enqueue(setDefaultHandler(NullApiRequestHandler()))

                        }
                        else -> {
                            var newNodeName = "[REF] "  + if (nodeName.isEmpty()) externalProjectName else nodeName
                            repo!!.AddNode(newNodeName, path, this)
                        }
                    }
                }
                else -> {
                    repo!!.AddNode(nodeName, path, this)
                }
            }
        }

        var callback2 = NodesServices.NodeZoomCallback {
            /*MainActivity.tempRootNodeDto = parentNode
            //repo!!.GetNodes(projectName, this)
            tvHelper!!.renderTree()
            toggleZoomNodeButton()*/
        }

        NodesServices.PromptNodeName(this, callback1, callback2)
    }

    override fun onLongClick(data: Object, position: Int): Boolean {
        val parentNode = data as NodeDto
        val callback2 = NodesServices.NodeDeleteCallback{
            repo!!.RemoveNode(parentNode.NodeName!!, parentNode.Path!!, this)
            api!!.deleteNodesInPath(parentNode.Path!!).enqueue(ApiManager.setArrayDefaultHandler(NullApiRequestHandler()))
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
        nodesData = collection.toCollection(ArrayList())

        if (nodesData == null)
            return

        parseNodesData()
    }

    override fun onError(error: String) {
        Toast.makeText(this, "$error", Toast.LENGTH_SHORT).show()
    }

    override fun onSuccess(obj: ResponseObject<NodeDto>) {
        graphType = 1
        repo!!.GetNodes(projectName, this)
    }
}
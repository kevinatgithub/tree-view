package app.kevs.treeview

import android.app.Activity
import android.util.DisplayMetrics
import app.kevs.treeview.network.models.Node
import com.google.gson.Gson
import de.blox.treeview.TreeNode
import de.blox.treeview.TreeView
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class TreeViewHelper(ctx : Activity, treeView: TreeView, nodeClick : NodeOnClick, nodes: List<Node>)  {

    val gson = Gson()
    val treeView : TreeView = treeView
    var renderer : Renderer? = null
    private val ctx : Activity = ctx
    private val nodeClick : NodeOnClick = nodeClick
    private val nodes = nodes
    private var resized = false

    public fun renderTree() {
        if (!resized){
            resizeView()
            resized = true
        }

        val root = parseNodes()
        if (root == null)
            throw Exception("Root Node not found!")

        renderer = Renderer(treeView, ctx, root, nodeClick)
        renderer!!.Render()
    }

    private fun resizeView() {
        val displayMetrics = DisplayMetrics()
        ctx.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        var width = displayMetrics.widthPixels
        treeView!!.layoutParams.height = height + 5000
        treeView!!.layoutParams.width = width + 30000
    }

    private fun parseNodes() : TreeNode? {
        var collection = ArrayList<Pair<Node, TreeNode>>()
        nodes.forEach {
            var treeNode = TreeNode(it)
            collection.add(Pair(it, treeNode))
        }

        if (collection.size > 1) {
            collection.forEach {
                val node = it.first
                val treeNode = it.second
                if (!node.Path.equals(node.ProjectName)){
                    val parent = collection.find { p ->
                        var pathArray = node.Path!!.split(Constants.PATH_DELIMITER)
                        var parentName = pathArray.get(pathArray.size - 1)
                        pathArray = pathArray.subList(0, pathArray.size - 1)
                        val newPath = pathArray.joinToString(Constants.PATH_DELIMITER)
                        p.first.Path == newPath && p.first.NodeName!!.equals(parentName)
                    }
                    if (parent != null)
                        parent.second.addChild(treeNode)
                }
            }
        }

        if (MainActivity.tempRootNode != null) {
            val node = MainActivity.tempRootNode
            val root = collection.find { p -> p.first.NodeName.equals(node!!.NodeName) && p.first.Path.equals(node!!.Path) && p.first.ProjectName.equals(node!!.ProjectName) }
            if (root != null)
                return root.second
        }else {
            val root = collection.find { p -> p.first.Path.equals(MainActivity.projectName) }
            if (root != null)
                return root.second
        }

        return null
    }

}
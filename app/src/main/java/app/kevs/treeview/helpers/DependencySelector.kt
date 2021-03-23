package app.kevs.treeview.helpers

import android.app.AlertDialog
import android.content.Context
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import app.kevs.treeview.Constants.Companion.NODE_TYPE_IMPLEMENTATION
import app.kevs.treeview.Constants.Companion.NODE_TYPE_LINK
import app.kevs.treeview.Constants.Companion.NODE_TYPE_MODEL
import app.kevs.treeview.Constants.Companion.PATH_DELIMITER
import app.kevs.treeview.network.models.NodeDto
import java.util.*

class DependencySelector(private val ctx: Context, private val node: NodeDto, private val nodes: ArrayList<NodeDto>, private val handler: NodesServices.DependencySelectedHandler){
    fun showDependencySelector() {
        val pathArray = node.Path!!.split(PATH_DELIMITER)
        val container = pathArray.last()
        val selectionObjects = when {
            container.toUpperCase(Locale.US).contains("CONTROLLER") -> {
                nodes.filter { n -> n.Type.equals(NODE_TYPE_IMPLEMENTATION) && n.NodeName!!.toUpperCase(Locale.US).endsWith("SERVICE") }
            }
            container.toUpperCase(Locale.US).contains("SERVICE") -> {
                nodes.filter { n -> n.Type.equals(NODE_TYPE_IMPLEMENTATION) && n.NodeName!!.toUpperCase(Locale.US).endsWith("REPOSITORY") }
            }
            container.toUpperCase(Locale.US).contains("REPOSITORY") -> {
                nodes.filter { n -> n.Type.equals(NODE_TYPE_MODEL) }
            }
            else -> nodes
        }

        val selection = selectionObjects.map { it.NodeName }

        val layout = LinearLayout(ctx)
        layout.layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layout.orientation = LinearLayout.VERTICAL
        val spinner = Spinner(ctx)
        spinner.adapter = ArrayAdapter(ctx, android.R.layout.simple_spinner_dropdown_item, selection)
        layout.addView(spinner)

        AlertDialog.Builder(ctx)
                .setTitle("Select Dependency")
                .setView(layout)
                .setPositiveButton("Add") { _, _ -> handler.onDependencySelected(selectionObjects.find { it.NodeName.equals(spinner.selectedItem.toString()) }!!, container)}
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel()}
            .show()
    }

    fun showLinkToNodeSelector() {
        val layout = LinearLayout(ctx)
        layout.layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layout.orientation = LinearLayout.VERTICAL
        val spinner = Spinner(ctx)
        spinner.adapter = ArrayAdapter(ctx, android.R.layout.simple_spinner_dropdown_item, nodes.map { it.NodeName })
        layout.addView(spinner)
        AlertDialog.Builder(ctx)
                .setTitle("Link to Node")
                .setView(layout)
                .setPositiveButton("Link") { _, _ ->
                    val targetNode = nodes.find { it.NodeName.equals(spinner.selectedItem.toString()) }
                    handler.onDependencySelected(targetNode, NODE_TYPE_LINK)
                }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .show()
    }
}
package app.kevs.treeview.network.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class NodeDto(
        @SerializedName("project") var ProjectName: String?,
        @SerializedName("nodeName") var NodeName: String?,
        @SerializedName("path") var Path: String?,
        @SerializedName("type") var Type: String? = null,
        @SerializedName("links") var Links: Array<NodeDto>? = null){

    fun Clone() : NodeDto{
        return Gson().fromJson(Gson().toJson(this), NodeDto::class.java)
    }

    fun AddLink(newLink : NodeDto){
        val links = this.Links
        var linksCollection = if (links == null) ArrayList<NodeDto>() else links.toCollection(ArrayList())
        linksCollection.add(newLink)
        this.Links = linksCollection.toTypedArray()
    }

    fun RemoveLink(link : NodeDto){
        val links = this.Links
        var linksCollection = if (links == null) ArrayList<NodeDto>() else links.toCollection(ArrayList())
        val toRemove = linksCollection.find { it.NodeName.equals(link.NodeName) && it.Path.equals(link.Path) }
        linksCollection.remove(toRemove)
        this.Links = linksCollection.toTypedArray()
    }
}
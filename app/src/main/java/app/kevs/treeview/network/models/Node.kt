package app.kevs.treeview.network.models

import com.google.gson.annotations.SerializedName

public class Node{

    constructor(ProjectName: String?, NodeName: String?, Path: String?) {
        this.ProjectName = ProjectName
        this.NodeName = NodeName
        this.Path = Path
    }

    @SerializedName("project")
    public var ProjectName : String? = null

    @SerializedName("nodeName")
    public var NodeName : String? = null

    @SerializedName("path")
    public var Path : String? = null
}
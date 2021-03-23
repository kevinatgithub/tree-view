package app.kevs.treeview.network.models

import com.google.gson.annotations.SerializedName

class NodeUpdateDto(@SerializedName("original") var original: NodeDto, @SerializedName("changes") var changes: NodeDto)
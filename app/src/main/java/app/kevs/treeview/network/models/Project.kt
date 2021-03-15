package app.kevs.treeview.network.models

import app.kevs.treeview.Constants
import com.google.gson.annotations.SerializedName

class Project(name: String, user: String, type: String){

    @SerializedName("name")
    var Name = name

    @SerializedName("key")
    var Key = user + Constants.PROJECT_DELIMITER + name

    @SerializedName("user")
    var User = user

    @SerializedName("type")
    var Type = type
}
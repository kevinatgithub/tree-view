package app.kevs.treeview.network.models

import com.google.gson.annotations.SerializedName

class LoginModel(username: String, password: String){
    @SerializedName("username")
    var Username = username
    @SerializedName("password")
    var Password = password
}
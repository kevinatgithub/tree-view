package app.kevs.treeview.network.models

import com.google.gson.annotations.SerializedName

class MigrateDto (@SerializedName("sourceBaseUrl") var SourceBaseUrl : String, @SerializedName("targetBaseUrl") var TargetBaseUrl : String)
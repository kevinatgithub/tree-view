package app.kevs.treeview

class Constants{
    companion object{
        val PATH_DELIMITER = "."
        val PROJECT_DELIMITER = "-"

        val NODE_TYPE_OTHER = ""
        val NODE_TYPE_ADD_REFERENCE = "Add Reference"
        val NODE_TYPE_MVC = "Model View Controller"
        val NODE_TYPE_SUB_PROJECT = "Strategy"
        val NODE_TYPE_IMPLEMENTATION = "Implementation Class"
        val NODE_TYPE_INTERFACE = "Interface Class"
        val NODE_TYPE_CONTROLLER = "Controller"
        val NODE_TYPE_MODEL = "Model"
        val NODE_TYPE_VIEW = "View"
        val NODE_TYPE_CRUD = "CRUD"
        val NODE_TYPE_DEPENDENCY_MODEL = "Model Dependency"
        val NODE_TYPE_DEPENDENCY_REPOSITORY = "Repository Dependency"
        val NODE_TYPE_DEPENDENCY_SERVICE = "Service Dependency"

        val PROJECT_TYPE_BLANK = ""
        val PROJECT_TYPE_MVC = "Model View Controller"
        val PROJECT_TYPE_MODEL_CLASS = "Model Class"

        val REFERENCE_TYPE_BLANK = "Blank"
        val REFERENCE_TYPE_Strategy = "Strategy"
    }
}
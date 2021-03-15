package app.kevs.treeview

class StringNode constructor(name : String) {
    var key : String = name
    var children = ArrayList<StringNode>()
    var path : String? = null
    fun attachTo(parentNode : StringNode){
        path = parentNode.path+key
        parentNode.children.add(this)
    }
}
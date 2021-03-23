package app.kevs.treeview.helpers

interface NodeOnClick{
    fun onClick(data : Any, position : Int)
    fun onLongClick(data : Any, position: Int) : Boolean
}
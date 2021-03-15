package app.kevs.treeview

import android.view.View

interface NodeOnClick{
    fun onClick(data : Object, position : Int)
    fun onLongClick(data : Object, position: Int) : Boolean
}
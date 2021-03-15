package app.kevs.treeview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.bakhtiyor.gradients.Gradients
import com.otaliastudios.zoom.ZoomLayout
import de.blox.treeview.TreeView

class ZoomActivity : AppCompatActivity(), NodeOnClick {

    var tvHelper : TreeViewHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom)

        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION and View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        supportActionBar?.hide()

        findViewById<ConstraintLayout>(R.id.container).background = Gradients.premiumDark()

        tvHelper = TreeViewHelper(this, findViewById<TreeView>(R.id.idTreeView), this, MainActivity.nodes!!)

        tvHelper!!.renderTree()

        val zoomLayout = findViewById<ZoomLayout>(R.id.zoomLayout)
        zoomLayout.zoomOut()

    }

    override fun onClick(data: Object, position: Int) {

    }

    override fun onLongClick(data: Object, position: Int): Boolean {
        return true
    }
}
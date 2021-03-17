package app.kevs.treeview

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.kevs.treeview.network.models.NodeDto
import app.kevs.treeview.network.models.Project
import com.bakhtiyor.gradients.Gradients
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.imu.flowerdelivery.network.ApiManager
import com.imu.flowerdelivery.network.callbacks.ArrayResponseHandler
import com.imu.flowerdelivery.network.callbacks.ObjectResponseHandler
import com.imu.flowerdelivery.network.interfaces.TreeApi
import com.imu.flowerdelivery.network.models.ResponseObject

class ProjectsActivity : AppCompatActivity(), ArrayResponseHandler<Project>,
    ObjectResponseHandler<Project> {
    companion object{
        var user : String? = null
        var projects = ArrayList<Project>()
        var api : TreeApi? = null
    }
    var rvProjects : RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_projects)

        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION and View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        supportActionBar?.hide()

        MainActivity.tempRootNodeDto = null

        findViewById<ConstraintLayout>(R.id.container).background = Gradients.premiumDark()

        api = ApiManager.getInstance(this)

        rvProjects = findViewById(R.id.rvProjects)
        rvProjects!!.layoutManager = LinearLayoutManager(this)

        findViewById<FloatingActionButton>(R.id.fabCreateProject).setOnClickListener {
            showCreateProjectDialog()
        }

        user = intent.getStringExtra("user").toString()

        refreshProjects()
    }

    private fun refreshProjects(){
        api!!.getProjectsForUser(user!!).enqueue(ApiManager.setArrayDefaultHandler(this))
    }

    private fun showCreateProjectDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Project Name")

        val layout = LinearLayout(this)
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layout.layoutParams = layoutParams
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(24, 16, 24, 16)

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        layout.addView(input)

        var spinnerItems = ArrayList<String>()
        spinnerItems.add(Constants.PROJECT_TYPE_BLANK)
        spinnerItems.add(Constants.PROJECT_TYPE_MVC)
        spinnerItems.add(Constants.PROJECT_TYPE_MODEL_CLASS)

        val spinner = Spinner(this)
        val spinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,spinnerItems)
        spinner.adapter = spinnerArrayAdapter
        layout.addView(spinner)

        builder.setView(layout)

        builder.setPositiveButton(
            "OK"
        ) { dialog, which -> createProject(input.text.toString(), spinner.selectedItem.toString()) }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.cancel() }

        builder.show()
    }

    private fun createProject(projectName: String, type: String) {
        api!!.addProject(Project(projectName, user!!,type)).enqueue(ApiManager.setDefaultHandler(this))
        val newProjectName = user + Constants.PROJECT_DELIMITER + projectName
        api!!.addNode(NodeDto(newProjectName, projectName, newProjectName)).enqueue(ApiManager.setDefaultHandler(NullApiRequestHandler()))
        val childPath = newProjectName+Constants.PATH_DELIMITER+projectName
        when(type){
            Constants.PROJECT_TYPE_MVC -> {
                api!!.addNode(NodeDto(newProjectName, "Controllers", childPath)).enqueue(ApiManager.setDefaultHandler(NullApiRequestHandler()))
                api!!.addNode(NodeDto(newProjectName, "Models", childPath)).enqueue(ApiManager.setDefaultHandler(NullApiRequestHandler()))
                api!!.addNode(NodeDto(newProjectName, "Views", childPath)).enqueue(ApiManager.setDefaultHandler(NullApiRequestHandler()))
            }
            Constants.PROJECT_TYPE_MODEL_CLASS -> {
                api!!.addNode(NodeDto(newProjectName, "Id", childPath)).enqueue(ApiManager.setDefaultHandler(NullApiRequestHandler()))
            }
        }
    }

    override fun onSuccess(collection: Array<Project>) {
        projects =  collection.toCollection(ArrayList())
        val adapter = ProjectsAdapter(this, collection.toList(), ProjectClick(this, api!!))
        rvProjects!!.adapter = adapter
        var divider = DividerItemDecoration(rvProjects!!.getContext(), DividerItemDecoration.VERTICAL)
        rvProjects!!.addItemDecoration(divider)
    }

    override fun onError(error: String) {
        Toast.makeText(this,error, Toast.LENGTH_LONG).show()
    }

    class ProjectsAdapter(ctx: Context, projects: List<Project>, handler: ProjectClickHandler) : RecyclerView.Adapter<ProjectsViewHolder>(){
        val ctx = ctx
        val projects = projects
        val handler = handler

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectsViewHolder {
            val view : View = LayoutInflater.from(ctx).inflate(R.layout.item_projects, parent, false)
            return ProjectsViewHolder(view)
        }

        override fun getItemCount(): Int {
            return projects.size
        }

        override fun onBindViewHolder(holder: ProjectsViewHolder, position: Int) {
            val project = projects.get(position)
            holder.projectName.setText(project.Name)
            holder.container.setOnClickListener { handler.onProjectClick(project) }
            holder.container.setOnLongClickListener { handler.onProjectLongClick(project) }
        }

    }

    class ProjectsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var container : ConstraintLayout = itemView.findViewById(R.id.container)
        var projectName : TextView = itemView.findViewById(R.id.projectName)

    }

    interface ProjectClickHandler{
        fun onProjectClick(project: Project)
        fun onProjectLongClick(project: Project) : Boolean
    }

    class ProjectClick(ctx: Context, api: TreeApi) : ProjectClickHandler,
        ArrayResponseHandler<NodeDto> {
        val ctx = ctx
        val api = api

        override fun onProjectClick(project: Project) {
            val intent = Intent(ctx, GraphViewActivity::class.java)
            intent.putExtra("project", project.Key)
            ctx.startActivity(intent)
        }

        override fun onProjectLongClick(project: Project) : Boolean {
            val builder = AlertDialog.Builder(ctx)
            builder.setTitle("Delete Project")
            builder.setPositiveButton(
                "Yes"
            ) { dialog, which -> deleteProject(project) }
            builder.setNegativeButton(
                "Cancel"
            ) { dialog, which -> dialog.cancel() }
            builder.show()
            return true
        }

        private fun deleteProject(project: Project) {
            val activity = ctx as ProjectsActivity
            api!!.deleteProject(project).enqueue(ApiManager.setDefaultHandler(activity))
            api!!.deleteNodesInProject(user!! + Constants.PROJECT_DELIMITER + project.Name).enqueue(ApiManager.setArrayDefaultHandler(this))
        }

        override fun onSuccess(collection: Array<NodeDto>) {

        }

        override fun onError(error: String) {

        }

    }

    override fun onSuccess(obj: ResponseObject<Project>) {
        api!!.getProjectsForUser(user!!).enqueue(ApiManager.setArrayDefaultHandler(this))
    }
}
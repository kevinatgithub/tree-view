package app.kevs.treeview

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.preference.PreferenceManager
import app.kevs.treeview.Constants.Companion.CONFIG_BASE_URL_KEY
import app.kevs.treeview.Constants.Companion.SESSION_KEY_USER
import app.kevs.treeview.helpers.ConfigApiHandler
import app.kevs.treeview.helpers.Prefs
import app.kevs.treeview.helpers.putAny
import app.kevs.treeview.network.interfaces.TreeConfigApi
import app.kevs.treeview.network.models.LoginModel
import app.kevs.treeview.network.models.User
import com.bakhtiyor.gradients.Gradients
import com.imu.flowerdelivery.network.ApiManager
import com.imu.flowerdelivery.network.callbacks.ObjectResponseHandler
import com.imu.flowerdelivery.network.interfaces.TreeApi
import com.imu.flowerdelivery.network.models.ResponseObject

class LoginActivity : AppCompatActivity(), ObjectResponseHandler<User> {
    private var username : TextView? = null
    private var password : TextView? = null
    private var login : Button? = null
    private var loading : ProgressBar? = null
    private var register : TextView? = null
    private var result : TextView? = null
    private var api : TreeApi? = null


    companion object{
        var configApi : TreeConfigApi? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Prefs = PreferenceManager.getDefaultSharedPreferences(this)
        configApi = ApiManager.getConfigInstance()
        if (Prefs.getString(CONFIG_BASE_URL_KEY, null) == null)
            configApi!!.getConfig().enqueue(ApiManager.setDefaultHandler(ConfigApiHandler()))
        supportActionBar?.hide()



        findViewById<ConstraintLayout>(R.id.container).background = Gradients.premiumDark()
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        result = findViewById(R.id.result)
        login = findViewById(R.id.login)
        loading = findViewById(R.id.loading)
        register = findViewById(R.id.register)

        login!!.setOnClickListener {
            attemptLogin()
        }

        register!!.setOnClickListener { register() }

        findViewById<TextView>(R.id.settings).setOnClickListener {
            /*configApi!!.getConfig().enqueue(ApiManager.setDefaultHandler(ConfigApiHandler(this)))
            api = ApiManager.getInstance(this)
            Toast.makeText(this, "Base Url ${Prefs.getString(CONFIG_BASE_URL_KEY, null)}", Toast.LENGTH_LONG).show()*/
            startActivity(Intent(this, SettingsActivity::class.java))
            finish()
        }

        val user = Prefs.getString(SESSION_KEY_USER,null)
        if (user != null)
            resumeAsUser(user)

        //Toast.makeText(this, ApiGenerator.APP_BASE_URL, Toast.LENGTH_SHORT).show()
        api = ApiManager.getInstance(this)
    }

    private fun attemptLogin() {
        if (username!!.text.isEmpty() || password!!.text.isEmpty()){
            Toast.makeText(applicationContext, "Login Failed!", Toast.LENGTH_SHORT).show()
            return
        }

        loading!!.visibility = View.VISIBLE
        result!!.visibility = View.GONE

        api!!.login(LoginModel(username!!.text.toString(), password!!.text.toString())).enqueue(ApiManager.setDefaultHandler(this))
    }

    private fun register(){
        if (username!!.text.isEmpty() || password!!.text.isEmpty()){
            Toast.makeText(applicationContext, "Login Failed!", Toast.LENGTH_SHORT).show()
            return
        }

        loading!!.visibility = View.VISIBLE
        result!!.visibility = View.GONE

        api!!.register(User(username = username!!.text.toString(), password = password!!.text.toString())).enqueue(ApiManager.setDefaultHandler(this))
    }

    override fun onSuccess(obj: ResponseObject<User>) {
        resumeAsUser(obj.Data!!.Username)
    }

    private fun resumeAsUser(username: String) {
        Prefs.putAny(SESSION_KEY_USER, username)
        loading!!.visibility = View.GONE
        val i = Intent(this, ProjectsActivity::class.java)
        i.putExtra("user", username)
        startActivity(i)
        finish()
    }

    override fun onError(error: String) {
        val baseUrl = Prefs.getString(CONFIG_BASE_URL_KEY, null)
        loading!!.visibility = View.GONE
        result!!.visibility = View.VISIBLE
        Toast.makeText(applicationContext, "Login Failed! $baseUrl", Toast.LENGTH_SHORT).show()
    }
}

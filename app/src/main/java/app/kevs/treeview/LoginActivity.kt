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
import app.kevs.treeview.network.models.LoginModel
import app.kevs.treeview.network.models.User
import com.bakhtiyor.gradients.Gradients
import com.imu.flowerdelivery.network.ApiManager
import com.imu.flowerdelivery.network.callbacks.ObjectResponseHandler
import com.imu.flowerdelivery.network.interfaces.TreeApi
import com.imu.flowerdelivery.network.models.ResponseObject

class LoginActivity : AppCompatActivity(), ObjectResponseHandler<User> {
    var username : TextView? = null
    var password : TextView? = null
    var login : Button? = null
    var loading : ProgressBar? = null
    var register : TextView? = null
    var result : TextView? = null
    var api : TreeApi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION and View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        supportActionBar?.hide()

        api = ApiManager.getInstance(this)

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
        loading!!.visibility = View.GONE
        val i = Intent(this, ProjectsActivity::class.java)
        i.putExtra("user", obj.Data!!.Username)
        startActivity(i)
        finish()
    }

    override fun onError(error: String) {
        loading!!.visibility = View.GONE
        result!!.visibility = View.VISIBLE
        Toast.makeText(applicationContext, "Login Failed!", Toast.LENGTH_SHORT).show()
    }
}

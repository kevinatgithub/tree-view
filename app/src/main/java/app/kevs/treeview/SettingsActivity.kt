package app.kevs.treeview

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import app.kevs.treeview.helpers.Prefs
import app.kevs.treeview.helpers.putAny
import app.kevs.treeview.network.interfaces.MigrateApi
import app.kevs.treeview.network.interfaces.TreeConfigApi
import app.kevs.treeview.network.models.ConfigDto
import app.kevs.treeview.network.models.MigrateDto
import com.imu.flowerdelivery.network.ApiGenerator
import com.imu.flowerdelivery.network.ApiManager
import com.imu.flowerdelivery.network.callbacks.ObjectResponseHandler
import com.imu.flowerdelivery.network.models.ResponseObject

class SettingsActivity : AppCompatActivity(), ObjectResponseHandler<ConfigDto> {
    private var sessionBaseUrl = ""
    private var txtBaseUrl : EditText? = null
    private var btnBaseUrl : Button? = null
    private var lblBaseUrl : TextView? = null

    private var txtMigrateSource : EditText? = null
    private var txtMigrateTarget : EditText? = null
    private var btnMigrate : Button? = null
    private var lblMigrate : TextView? = null

    private val _stateNormal = 1
    private val _stateBaseUrlSaving = 2
    private val _stateMigrating = 3

    private var treeConfigApi : TreeConfigApi? = null
    private var migrateApi : MigrateApi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        treeConfigApi = ApiManager.getConfigInstance()
        migrateApi = ApiManager.getMigrateInstance()

        txtBaseUrl = findViewById(R.id.txtBaseUrl)
        btnBaseUrl = findViewById(R.id.btnUpdateBaseUrl)
        lblBaseUrl = findViewById(R.id.lblBaseUrlSaving)

        txtMigrateSource = findViewById(R.id.txtMigrateSource)
        txtMigrateTarget = findViewById(R.id.txtMigrateTarget)
        btnMigrate = findViewById(R.id.btnMigrate)
        lblMigrate = findViewById(R.id.lblMigrateLoading)

        Prefs = PreferenceManager.getDefaultSharedPreferences(this)
        sessionBaseUrl = Prefs.getString(Constants.CONFIG_BASE_URL_KEY, null)!!

        setState(_stateNormal)

        btnBaseUrl!!.setOnClickListener { updateBaseUrl() }
        btnMigrate!!.setOnClickListener { migrate() }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun updateBaseUrl() {
        val baseUrl = txtBaseUrl!!.text.toString()
        if (baseUrl.isEmpty()){
            Toast.makeText(this, "Please enter base url!", Toast.LENGTH_SHORT).show()
            return
        }

        if (baseUrl == sessionBaseUrl){
            Toast.makeText(this, "No changes required!", Toast.LENGTH_SHORT).show()
            return
        }
        setState(_stateBaseUrlSaving)
        treeConfigApi!!.postConfig(ConfigDto(baseUrl)).enqueue(ApiManager.setDefaultHandler(this))
    }

    private fun migrate() {
        val sourceUrl = txtMigrateSource!!.text.toString()
        val targetUrl = txtMigrateTarget!!.text.toString()
        if (sourceUrl.isEmpty() || targetUrl.isEmpty()){
            Toast.makeText(this, "Invalid Source / Target URL", Toast.LENGTH_LONG).show()
            return
        }

        if (sourceUrl == targetUrl){
            Toast.makeText(this, "Target URL can't be the same as source!", Toast.LENGTH_LONG).show()
            return
        }

        setState(_stateMigrating)

        migrateApi!!.migrate(Constants.MIGRATION_FUNCTION_APP_CODE,MigrateDto(sourceUrl, targetUrl)).enqueue(ApiManager.setDefaultHandler(object : ObjectResponseHandler<MigrateDto>{
            override fun onSuccess(obj: ResponseObject<MigrateDto>) {
                this@SettingsActivity.setState(_stateNormal)
                Toast.makeText(this@SettingsActivity, "Migration request sent! Please wait for at least 10 seconds..", Toast.LENGTH_LONG).show()
            }

            override fun onError(error: String) {
                this@SettingsActivity.onError(error)
            }

        }))
    }

    private fun setState(state: Int) {
        txtBaseUrl!!.setText(sessionBaseUrl)
        txtMigrateSource!!.setText(sessionBaseUrl)

        when(state){
            _stateNormal -> {
                txtBaseUrl!!.isEnabled = true
                btnBaseUrl!!.isEnabled = true
                lblBaseUrl!!.visibility = View.INVISIBLE
                txtMigrateSource!!.isEnabled = true
                txtMigrateTarget!!.isEnabled = true
                btnMigrate!!.isEnabled = true
                lblMigrate!!.visibility = View.INVISIBLE
            }
            _stateBaseUrlSaving -> {
                txtBaseUrl!!.isEnabled = false
                btnBaseUrl!!.isEnabled = false
                lblBaseUrl!!.visibility = View.VISIBLE
                txtMigrateSource!!.isEnabled = true
                txtMigrateTarget!!.isEnabled = true
                btnMigrate!!.isEnabled = true
                lblMigrate!!.visibility = View.INVISIBLE
            }
            _stateMigrating -> {
                txtBaseUrl!!.isEnabled = true
                btnBaseUrl!!.isEnabled = true
                lblBaseUrl!!.visibility = View.INVISIBLE
                txtMigrateSource!!.isEnabled = false
                txtMigrateTarget!!.isEnabled = false
                btnMigrate!!.isEnabled = false
                lblMigrate!!.visibility = View.VISIBLE
            }
        }
    }

    override fun onSuccess(obj: ResponseObject<ConfigDto>) {
        Prefs.putAny(Constants.CONFIG_BASE_URL_KEY, obj.Data!!.baseUrl.toString())
        sessionBaseUrl = obj.Data!!.baseUrl.toString()
        ApiGenerator.APP_BASE_URL = obj.Data!!.baseUrl.toString()
        ApiGenerator.applyNewBaseUrl()
        setState(_stateNormal)
        Toast.makeText(this, "Base URL Updated!", Toast.LENGTH_LONG).show()
    }

    override fun onError(error: String) {
        setState(_stateNormal)
    }
}
package artictactoe.ui

import android.Manifest
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import artictactoe.handlers.ArHandler
import artictactoe.managers.PermissionManager
import com.google.ar.sceneform.ux.ArFragment
import tictactoe.R

/**
 * Created by Iván Carrasco Alonso on 02/12/2018.
 */
class MainActivity : AppCompatActivity() {
    val customArFragment by lazy{
        supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as CustomArFragment
    }
    val arHandler by lazy {
        ArHandler(customArFragment,this.applicationContext)
    }
    val clearButton by lazy {
        findViewById<Button>(R.id.clear_button).setOnClickListener {
            arHandler.cloudAnchor = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        PermissionManager.instance.checkIsSupportedDeviceOrFinish(this)
        PermissionManager.instance.checkPermission(
            this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.INTERNET),
            PermissionManager.INITIAL_PERMISSION,
            {
                Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_LONG).show()
            }
        )

        customArFragment.setOnTapArPlaneListener()

    }
}
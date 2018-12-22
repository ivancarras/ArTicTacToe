package artictactoe.ui

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import artictactoe.handlers.ArHandler
import artictactoe.handlers.ArHandler.AppAnchorState
import artictactoe.managers.PermissionManager
import tictactoe.R


/**
 * Created by Iván Carrasco Alonso on 02/12/2018.
 */
class MainActivity : AppCompatActivity() {
    private val customArFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as CustomArFragment
    }
    private val arHandler by lazy {
        ArHandler(customArFragment, this.applicationContext)
    }
    private val clearButton by lazy {
        findViewById<Button>(R.id.clear_button)
    }

    private val resolveButton by lazy {
        findViewById<Button>(R.id.resolve_button)
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
        clearButton.setOnClickListener {
            arHandler.cloudAnchor = null
        }

        resolveButton.setOnClickListener {
            if (arHandler.cloudAnchor != null) {
                Toast.makeText(this, R.string.hosting_clear, Toast.LENGTH_LONG).show()
            } else {
                val dialog = ResolveDialogFragment()
                dialog.setOkListener(object : ResolveDialogFragment.OkListener {
                    override fun onOkPressed(dialogValue: String) {
                        onResolveOkPressed(dialogValue)
                        dialog.show(supportFragmentManager, "Resolve");
                    }
                })
            }
        }


        customArFragment.setOnTapArPlaneListener { hitResult, _, _ ->
            arHandler.cloudAnchor =
                    customArFragment.arSceneView.session.hostCloudAnchor(hitResult.createAnchor())

            arHandler.appAnchorState = ArHandler.AppAnchorState.HOSTING
            Toast.makeText(applicationContext, R.string.hosting_anchor, Toast.LENGTH_LONG).show()

            arHandler.cloudAnchor?.let {
                arHandler.placeObject(it, Uri.parse("fox.sfb"))
            }
        }
        customArFragment.arSceneView.scene.addOnUpdateListener(arHandler::onUpdateFrame)
    }

    private fun onResolveOkPressed(dialogValue: String) {
        val shortCode = Integer.parseInt(dialogValue)
        arHandler.storeManager.getCloudAnchorID(shortCode) { cloudAnchorId ->
            val resolvedAnchor =
                customArFragment.arSceneView.session.resolveCloudAnchor(cloudAnchorId as String)
            arHandler.cloudAnchor = resolvedAnchor
            arHandler.cloudAnchor?.let {
                arHandler.placeObject(it, Uri.parse("Fox.sfb"))
            }
            Toast.makeText(
                applicationContext,
                this.getString(R.string.hosting_resolving),
                Toast.LENGTH_LONG
            ).show()
            arHandler.appAnchorState = AppAnchorState.RESOLVING
        }
    }
}
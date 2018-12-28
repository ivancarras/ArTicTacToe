package artictactoe.ui

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import artictactoe.handlers.ArHandler
import artictactoe.handlers.ArHandler.AppAnchorState
import artictactoe.managers.PermissionManager
import artictactoe.managers.StoreManager
import tictactoe.R


/**
 * Created by Iván Carrasco Alonso on 02/12/2018.
 */
class MainActivity : AppCompatActivity() {
    private val customArFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as CustomArFragment
    }
    private val arHandler by lazy {
        ArHandler(customArFragment,this,snackbarHelper)
    }
    private val clearButton by lazy {
        findViewById<Button>(R.id.clear_button)
    }

    private val resolveButton by lazy {
        findViewById<Button>(R.id.resolve_button)
    }

    private val snackbarHelper by lazy {
        SnackbarHelper()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        PermissionManager.instance.checkIsSupportedDeviceOrFinish(this)
        PermissionManager.instance.checkPermission(
            this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.INTERNET),
            PermissionManager.INITIAL_PERMISSION
        ) {
            snackbarHelper.showMessage(this, getString(R.string.permission_granted))
        }
        clearButton.setOnClickListener {
            arHandler.cloudAnchor = null
        }

        resolveButton.setOnClickListener {
            if (arHandler.cloudAnchor != null) {
                snackbarHelper.showMessage(this, getString(R.string.hosting_clear))
            } else {
                val dialog = ResolveDialogFragment()
                dialog.setOkListener(object : ResolveDialogFragment.OkListener {
                    override fun onOkPressed(dialogValue: String) {
                        onResolveOkPressed(dialogValue)
                        dialog.show(supportFragmentManager, "Resolve")
                    }
                })
            }
        }


        customArFragment.setOnTapArPlaneListener { hitResult, _, _ ->
            arHandler.cloudAnchor =
                    customArFragment.arSceneView.session.hostCloudAnchor(hitResult.createAnchor())

            arHandler.appAnchorState = ArHandler.AppAnchorState.HOSTING
            snackbarHelper.showMessage(this, getString(R.string.hosting_anchor))

            arHandler.cloudAnchor?.let {
                arHandler.placeObject(it, Uri.parse("ArcticFox_Posed.sfb"))
            }
        }
        customArFragment.arSceneView.scene.addOnUpdateListener(arHandler::onUpdateFrame)
    }

    private fun onResolveOkPressed(dialogValue: String) {
        val shortCode = Integer.parseInt(dialogValue)
        arHandler.storeManager.getCloudAnchorID(shortCode,
            object : StoreManager.CloudAnchorIdListener {
                override fun onCloudAnchorIdAvailable(cloudAnchorId: String?) {
                    val resolvedAnchor =
                        customArFragment.arSceneView.session.resolveCloudAnchor(cloudAnchorId as String)
                    arHandler.cloudAnchor = resolvedAnchor
                    arHandler.cloudAnchor?.let {
                        arHandler.placeObject(it, Uri.parse("ArcticFox_Posed.sfb"))
                    }
                    snackbarHelper.showMessage(
                        this@MainActivity
                        ,
                        getString(R.string.hosting_resolving)
                    )
                    arHandler.appAnchorState = AppAnchorState.RESOLVING
                }
            })
    }
}
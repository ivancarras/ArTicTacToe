package artictactoe.mvvm.view

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import artictactoe.managers.PermissionManager
import artictactoe.mvvm.viewmodels.GameViewModel
import tictactoe.R


/**
 * Created by Iván Carrasco Alonso on 02/12/2018.
 */
class MainActivity : AppCompatActivity() {
    private val customArFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as CustomArFragment
    }
    /*    private val arHandler by lazy {
            DeprecatedArHandler(customArFragment, this, snackbarHelper)
        }*/
    private val snackbarHelper by lazy {
        SnackbarHelper()
    }

    val gameViewModel by lazy {
        ViewModelProviders.of(this).get(GameViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        /*gameViewModel.repository.liveData.observe(this, Observer {
             Log.i("MainActivity", "Game update")
             //Update UI
             //Update AR
         })**/

        PermissionManager.instance.checkIsSupportedDeviceOrFinish(this)
        PermissionManager.instance.checkPermission(
            this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.INTERNET),
            PermissionManager.INITIAL_PERMISSION
        ) {
            snackbarHelper.showMessage(this, getString(R.string.permission_granted))
        }

        /**resolveButton.setOnClickListener {
        if (arHandler.cloudAnchor != null) {
        snackbarHelper.showMessage(this, getString(R.string.hosting_clear))
        } else {
        val dialog = ResolveDialogFragment()
        dialog.setOkListener(object : ResolveDialogFragment.OkListener {
        override fun onOkPressed(dialogValue: String) {
        onResolveOkPressed(dialogValue)
        }
        })
        dialog.show(supportFragmentManager, "dialog")
        }
        }**/

        /**customArFragment.setOnTapArPlaneListener { hitResult, _, _ ->
        arHandler.createCloudAnchor(customArFragment, hitResult.createAnchor())
        }
        customArFragment.arSceneView.scene.addOnUpdateListener(arHandler::onUpdateFrame)**/

    }

    private fun onResolveOkPressed(dialogValue: String) {
        /*val shortCode = Integer.parseInt(dialogValue)
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
            })*/
    }
}
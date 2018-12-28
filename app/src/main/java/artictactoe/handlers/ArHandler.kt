package artictactoe.handlers

import android.app.Activity
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.util.Log
import artictactoe.managers.StoreManager
import artictactoe.ui.SnackbarHelper
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import tictactoe.R


/**
 * Created by Ivan Carrasco Alonso on 03/12/2018.
 */

class ArHandler(
    val fragment: ArFragment,
    val activity: Activity,
    val snackbarHelper: SnackbarHelper
) {
    enum class AppAnchorState {
        NONE, HOSTING, HOSTED, RESOLVING, RESOLVED
    }

    var cloudAnchor: Anchor? = null
        set(anchor) {
            cloudAnchor?.let {
                it.detach()
            }
            field = anchor
            appAnchorState = AppAnchorState.NONE
        }
    var appAnchorState = AppAnchorState.NONE
    val storeManager: StoreManager by lazy {
        artictactoe.managers.StoreManager(activity.applicationContext)
    }

    fun placeObject(anchor: Anchor, model: Uri) {
        ModelRenderable.builder()
            .setSource(fragment.context, model)
            .build()
            .thenAccept { renderable -> addNodeToScene(anchor, renderable) }
            .exceptionally { throwable ->
                val builder = AlertDialog.Builder(activity.applicationContext)
                builder.setMessage(throwable.message)
                    .setTitle("Error!")
                val dialog = builder.create()
                dialog.show()
                null
            }
    }

    fun addNodeToScene(anchor: Anchor, renderable: Renderable) {
        val anchorNode = AnchorNode(anchor)
        val node = TransformableNode(fragment.transformationSystem)
        node.renderable = renderable
        node.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
        node.select()
    }

    fun onUpdateFrame(frameTime: FrameTime) {
        checkUpdatedAnchor()
    }

    @Synchronized
    private fun checkUpdatedAnchor() {
        if (appAnchorState != AppAnchorState.HOSTING && appAnchorState != AppAnchorState.RESOLVING) {
            return
        }
        val cloudState = cloudAnchor?.cloudAnchorState
        Log.i("CheckUpdatedAnchor", cloudState.toString())
        if (appAnchorState === AppAnchorState.HOSTING) {
            if (cloudState != null) {
                if (cloudState.isError) {
                    snackbarHelper.showMessage(
                        activity,
                        activity.getString(R.string.hosting_error) + cloudState
                    )
                    appAnchorState = AppAnchorState.NONE
                } else if (cloudState == Anchor.CloudAnchorState.SUCCESS) {
                    storeManager.nextShortCode(object : StoreManager.ShortCodeListener {
                        override fun onShortCodeAvailable(shortCode: Int?) {
                            if (shortCode == null) {
                                snackbarHelper.showMessage(
                                    activity, "Could not get shortCode"
                                )
                                return
                            }
                            cloudAnchor?.let {
                                storeManager.storeUsingShortCode(shortCode, it.cloudAnchorId)
                            }


                            snackbarHelper.showMessage(
                                activity, "Anchor hosted! Cloud Short Code: " +
                                        shortCode
                            )
                            appAnchorState = AppAnchorState.HOSTED
                        }
                    })
                }

            }
        } else if (appAnchorState == AppAnchorState.RESOLVING) {
            if (cloudState != null) {
                if (cloudState.isError) {
                    snackbarHelper.showMessage(
                        activity, "Error resolving anchor.. "
                                + cloudState
                    )
                    appAnchorState = AppAnchorState.NONE
                } else if (cloudState == Anchor.CloudAnchorState.SUCCESS) {
                    snackbarHelper.showMessage(
                        activity, "Anchor resolved successfully"
                    )
                    appAnchorState = AppAnchorState.RESOLVED
                }
            }
        }
    }
}
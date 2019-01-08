package artictactoe.handlers

import android.app.Activity
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.util.Log
import artictactoe.managers.StoreManager
import artictactoe.mvvm.ui.CustomArFragment
import artictactoe.mvvm.ui.SnackbarHelper
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

    enum class AnchorTask {
        UPDATE, CREATE
    }

    var cloudAnchor: Anchor? = null
        set(anchor) {
            cloudAnchor?.detach()

            field = anchor
            appAnchorState = AppAnchorState.NONE
        }
    var appAnchorState = AppAnchorState.NONE
    val storeManager: StoreManager by lazy {
        artictactoe.managers.StoreManager(activity.applicationContext)
    }
    var anchorTask = AnchorTask.CREATE
    var myShortCode: Int? = null

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
                    appAnchorState = AppAnchorState.HOSTED

                    if (anchorTask == AnchorTask.CREATE) {
                        storeManager.nextShortCode(object : StoreManager.ShortCodeListener {
                            override fun onShortCodeAvailable(shortCode: Int?) {
                                myShortCode = shortCode
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

                            }
                        })
                    } else if (anchorTask == AnchorTask.UPDATE) {
                        myShortCode?.let { sc ->
                            cloudAnchor?.let { ca ->
                                storeManager.updateCloudAnchorID(
                                    sc,
                                    ca.cloudAnchorId
                                )
                                snackbarHelper.showMessage(
                                    activity, "Anchor Updated!: "+myShortCode
                                )
                            }
                        }
                    }
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

    fun createCloudAnchor(customArFragment: CustomArFragment, anchor: Anchor) {
        this.cloudAnchor =
                customArFragment.arSceneView.session.hostCloudAnchor(anchor)
        this.anchorTask = AnchorTask.CREATE
        this.appAnchorState = ArHandler.AppAnchorState.HOSTING
        snackbarHelper.showMessage(activity, activity.getString(R.string.hosting_anchor))

        this.cloudAnchor?.let {
            this.placeObject(it, Uri.parse("ArcticFox_Posed.sfb"))
        }
    }

}
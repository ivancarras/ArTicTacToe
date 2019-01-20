package artictactoe.handlers

import android.net.Uri
import android.util.Log
import artictactoe.mvvm.view.CustomArFragment
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

/**
 * Created by Iván Carrasco Alonso on 19/01/2019.
 */
class ArHandler {

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
            appAnchorState = ArHandler.AppAnchorState.NONE
        }

    var appAnchorState = ArHandler.AppAnchorState.NONE

    var anchorTask = ArHandler.AnchorTask.CREATE

    fun createCloudAnchor(
        customArFragment: CustomArFragment,
        anchor: Anchor,
        getCloudAnchor: (cloudAnchorID: String) -> Unit
    ) {

        this.cloudAnchor =
                customArFragment.arSceneView.session.hostCloudAnchor(anchor)
        this.anchorTask = ArHandler.AnchorTask.CREATE
        this.appAnchorState = ArHandler.AppAnchorState.HOSTING

        customArFragment.arSceneView.scene.addOnUpdateListener {
            checkUpdatedAnchor(getCloudAnchor)
        }
        //snackbarHelper.showMessage(activity, activity.getString(R.string.hosting_anchor))


        this.cloudAnchor?.let {
            this.placeObject(it, Uri.parse("ArcticFox_Posed.sfb"), customArFragment)
        }
    }

    @Synchronized
    private fun checkUpdatedAnchor(getCloudAnchor: (cloudAnchorID: String) -> Unit) {
        if (appAnchorState != ArHandler.AppAnchorState.HOSTING && appAnchorState != ArHandler.AppAnchorState.RESOLVING) {
            return
        }
        val cloudState = cloudAnchor?.cloudAnchorState
        Log.i("CheckUpdatedAnchor", cloudState.toString())

        if (appAnchorState === ArHandler.AppAnchorState.HOSTING) {
            if (cloudState != null) {
                if (cloudState.isError) {
                    /*
                    snackbarHelper.showMessage(
                                   activity,
                                   activity.getString(R.string.hosting_error) + cloudState
                               )*/
                    appAnchorState = ArHandler.AppAnchorState.NONE
                } else if (cloudState == Anchor.CloudAnchorState.SUCCESS) {
                    appAnchorState = ArHandler.AppAnchorState.HOSTED

                    if (anchorTask == ArHandler.AnchorTask.CREATE) {
                        cloudAnchor?.let {
                            getCloudAnchor(it.cloudAnchorId)
                        }

                        /*storeManager.nextShortCode(object : StoreManager.ShortCodeListener {
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
                        })*/
                    } else if (anchorTask == ArHandler.AnchorTask.UPDATE) {
                        cloudAnchor?.let {
                            getCloudAnchor(it.cloudAnchorId)
                        }

                        /* myShortCode?.let { sc ->
                             cloudAnchor?.let { ca ->
                                 storeManager.updateCloudAnchorID(
                                     sc,
                                     ca.cloudAnchorId
                                 )
                                 snackbarHelper.showMessage(
                                     activity, "Anchor Updated!: " + myShortCode
                                 )
                             }
                         }*/
                    }
                }

            }
        } else if (appAnchorState == ArHandler.AppAnchorState.RESOLVING) {
            if (cloudState != null) {
                if (cloudState.isError) {
                    /*       snackbarHelper.showMessage(
                               activity, "Error resolving anchor.. "
                                       + cloudState
                           )*/
                    appAnchorState = ArHandler.AppAnchorState.NONE
                } else if (cloudState == Anchor.CloudAnchorState.SUCCESS) {
/*
                    snackbarHelper.showMessage(
                        activity, "Anchor resolved successfully"
                    )
*/
                    appAnchorState = ArHandler.AppAnchorState.RESOLVED
                }
            }
        }
    }

    fun placeObject(anchor: Anchor, model: Uri, fragment: ArFragment) {
        ModelRenderable.builder()
            .setSource(fragment.context, model)
            .build()
            .thenAccept { renderable -> addNodeToScene(anchor, renderable, fragment) }
            .exceptionally { throwable ->
                /*val builder = AlertDialog.Builder(activity.applicationContext)
                builder.setMessage(throwable.message)
                    .setTitle("Error!")
                val dialog = builder.create()
                dialog.show()*/
                null
            }
    }

    fun addNodeToScene(anchor: Anchor, renderable: Renderable, fragment: ArFragment) {
        val anchorNode = AnchorNode(anchor)
        val node = TransformableNode(fragment.transformationSystem)
        node.renderable = renderable
        node.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
        node.select()
    }

    fun resolveAnchor(cloudAnchorID: String, customArFragment: CustomArFragment) {
        val resolvedAnchor =
            customArFragment.arSceneView.session.resolveCloudAnchor(cloudAnchorID)
        cloudAnchor = resolvedAnchor
        cloudAnchor?.let {
            placeObject(it, Uri.parse("ArcticFox_Posed.sfb"), customArFragment)
        }
        /*snackbarHelper.showMessage(
            this@MainActivity
            ,
            getString(R.string.hosting_resolving)
        )*/
        appAnchorState = AppAnchorState.RESOLVING
    }
}
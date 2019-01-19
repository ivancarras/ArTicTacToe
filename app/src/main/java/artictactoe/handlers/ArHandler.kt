package artictactoe.handlers

import android.util.Log
import artictactoe.mvvm.view.CustomArFragment
import com.google.ar.core.Anchor

/**
 * Created by Iván Carrasco Alonso on 19/01/2019.
 */
class ArHandler() {

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

        /*
            this.cloudAnchor?.let {
            this.placeObject(it, Uri.parse("ArcticFox_Posed.sfb"))
        }*/
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
}
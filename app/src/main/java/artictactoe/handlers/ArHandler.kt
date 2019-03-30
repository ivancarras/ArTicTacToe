package artictactoe.handlers

import android.net.Uri
import android.util.Log
import artictactoe.mvvm.model.Cell
import artictactoe.mvvm.view.CustomArFragment
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.schedulers.Schedulers

/**
 * Created by Iván Carrasco Alonso on 19/01/2019.
 */
class ArHandler : IArHandler {
    private var cloudAnchor: AnchorNode? = null
        set(anchor) {
            cloudAnchor?.anchor?.detach()
            field = anchor
            appAnchorState = ArHandler.AppAnchorState.NONE
        }
    private var appAnchorState = ArHandler.AppAnchorState.NONE
    private var anchorTask = ArHandler.AnchorTask.CREATE

    private val arDrawer = ArDrawer()

    override fun createCloudAnchor(
        customArFragment: CustomArFragment,
        anchor: Anchor
    ): Single<String> {

        this.cloudAnchor?.anchor =
            customArFragment.arSceneView.session?.hostCloudAnchor(anchor)
        this.anchorTask = ArHandler.AnchorTask.CREATE
        this.appAnchorState = ArHandler.AppAnchorState.HOSTING

        return Single.create<String> { emitter ->
            customArFragment.arSceneView.scene.addOnUpdateListener {
                checkUpdatedAnchor(emitter)
            }
        }.subscribeOn(Schedulers.single())
    }

    //Temporal
    override fun resolveCloudAnchor(cloudAnchorID: String, customArFragment: CustomArFragment) {
        val resolvedAnchor =
            customArFragment.arSceneView.session?.resolveCloudAnchor(cloudAnchorID)
        cloudAnchor?.anchor = resolvedAnchor
        //We have to render the board
        cloudAnchor?.let {
           // val par
            arDrawer.placeObject(it, Uri.parse("ArcticFox_Posed.sfb"), customArFragment, true)
        }
        appAnchorState = AppAnchorState.RESOLVING
    }

    override fun redrawCells(cells: List<List<Cell>>) {

    }

    private fun checkUpdatedAnchor(emitter: SingleEmitter<String>) {
         if (appAnchorState != ArHandler.AppAnchorState.HOSTING && appAnchorState != ArHandler.AppAnchorState.RESOLVING) {
            return
        }
        val cloudState = cloudAnchor?.anchor?.cloudAnchorState
        Log.i("CheckUpdatedAnchor", cloudState.toString())

        if (appAnchorState === ArHandler.AppAnchorState.HOSTING) {
            if (cloudState != null) {
                if (cloudState.isError) {
                    appAnchorState = ArHandler.AppAnchorState.NONE
                    emitter.onError(Throwable("Server error"))

                } else if (cloudState == Anchor.CloudAnchorState.SUCCESS) {
                    appAnchorState = ArHandler.AppAnchorState.HOSTED

                    if (anchorTask == ArHandler.AnchorTask.CREATE) {
                        cloudAnchor?.anchor?.let {
                            emitter.onSuccess(it.cloudAnchorId)
                        }
                    } else if (anchorTask == ArHandler.AnchorTask.UPDATE) {
                        cloudAnchor?.anchor?.let {
                            emitter.onSuccess(it.cloudAnchorId)
                        }
                    }
                }

            }
        } else if (appAnchorState == ArHandler.AppAnchorState.RESOLVING) {
            if (cloudState != null) {
                if (cloudState.isError) {
                    appAnchorState = ArHandler.AppAnchorState.NONE
                    emitter.onError(Throwable("Server error"))
                } else if (cloudState == Anchor.CloudAnchorState.SUCCESS) {
                    appAnchorState = ArHandler.AppAnchorState.RESOLVED
                    cloudAnchor?.anchor?.let {
                        emitter.onSuccess(it.cloudAnchorId)
                    }
                }
            }
        }
    }

    enum class AppAnchorState {
        NONE, HOSTING, HOSTED, RESOLVING, RESOLVED
    }

    enum class AnchorTask {
        UPDATE, CREATE
    }
}
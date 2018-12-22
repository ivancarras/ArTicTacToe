package artictactoe.handlers

import android.content.Context
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.widget.Toast
import artictactoe.managers.StoreManager
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

class ArHandler(val fragment: ArFragment, val context: Context) {
    enum class AppAnchorState {
        NONE, HOSTING, HOSTED, RESOLVING
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
        artictactoe.managers.StoreManager(context)
    }

    fun placeObject(anchor: Anchor, model: Uri) {
        ModelRenderable.builder()
            .setSource(fragment.context, model)
            .build()
            .thenAccept { renderable -> addNodeToScene(anchor, renderable) }
            .exceptionally { throwable ->
                val builder = AlertDialog.Builder(context)
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
        if (appAnchorState !== AppAnchorState.HOSTING) {
            return
        }
        val cloudState = cloudAnchor?.cloudAnchorState

        if (appAnchorState === AppAnchorState.HOSTING) {
            if (cloudState != null) {
                if (cloudState.isError) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.hosting_error) + cloudState,
                        Toast.LENGTH_LONG
                    ).show()
                    appAnchorState = AppAnchorState.NONE
                } else if (cloudState == Anchor.CloudAnchorState.SUCCESS) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.hosting_success) + cloudAnchor?.cloudAnchorId,
                        Toast.LENGTH_LONG
                    ).show()
                    appAnchorState = AppAnchorState.HOSTED
                }
            }
        }
    }
}
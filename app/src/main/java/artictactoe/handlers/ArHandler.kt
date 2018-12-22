package artictactoe.handlers

import android.content.Context
import android.net.Uri
import android.support.v7.app.AlertDialog
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode


/**
 * Created by Ivan Carrasco Alonso on 03/12/2018.
 */

class ArHandler(val fragment:ArFragment ,val context:Context) {
    var cloudAnchor: Anchor? = null
        set(anchor){
            cloudAnchor?.let{
                it.detach()
            }
            field = anchor
        }


    private fun placeObject(anchor: Anchor, model: Uri){
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
    private fun addNodeToScene(anchor:Anchor,renderable: Renderable){
        val anchorNode = AnchorNode(anchor)
        val node = TransformableNode(fragment.transformationSystem)
        node.renderable = renderable
        node.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
        node.select()
    }
}
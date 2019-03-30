package artictactoe.handlers

import android.net.Uri
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

/**
 * Created by Iván Carrasco Alonso on 24/03/2019.
 */
class ArDrawer {
    fun placeObject(parent: Node, model: Uri, fragment: ArFragment, isParent: Boolean = false) {
        ModelRenderable.builder()
            .setSource(fragment.context, model)
            .build()
            .thenAccept { renderable -> addNodeToScene(parent, renderable, fragment, isParent) }
            .exceptionally { throwable ->
                /*val builder = AlertDialog.Builder(activity.applicationContext)
                builder.setMessage(throwable.message)
                    .setTitle("Error!")
                val dialog = builder.create()
                dialog.show()*/
                null
            }
    }

    private fun addNodeToScene(parent: Node, renderable: Renderable, fragment: ArFragment, isParent: Boolean) {
        val childNode = TransformableNode(fragment.transformationSystem)
        childNode.renderable = renderable
        if (!isParent)
            parent.addChild(childNode)
    }
}
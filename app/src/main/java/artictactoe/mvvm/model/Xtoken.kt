package artictactoe.mvvm.model

import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable

/**
 * Created by Iván Carrasco on 06/01/2019.
 */

class Xtoken(
    nodeId: Int,
    modelRenderable: ModelRenderable,
    positionVector: Vector3,
    rotation: Float,
    parent: Node
) : BaseNode(nodeId, modelRenderable, positionVector, rotation, parent) {
    override val nodetype: NodeType
        get() = NodeType.X
}
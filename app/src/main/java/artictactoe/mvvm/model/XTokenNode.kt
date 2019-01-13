package artictactoe.mvvm.model

import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable

/**
 * Created by Iván Carrasco on 12/01/2019.
 */
class XTokenNode(
    player: Player,
    nodeId: Int,
    modelRenderable: ModelRenderable,
    positionVector: Vector3,
    rotation: Float,
    parent: Node
) : TokenNode(player, nodeId, modelRenderable, positionVector, rotation, parent) {
}
package artictactoe.mvvm.model

import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable

/**
 * Created by Iván Carrasco on 31/12/2018.
 */
abstract class BaseNode(
    nodeId:Int,
    modelRenderable: ModelRenderable,
    positionVector: Vector3,
    rotation: Float,
    parent: Node
) : Node() {
    abstract val nodetype: NodeType

    enum class NodeType {
        BOARD, CIRCLE, X
    }

    init {
        super.setParent(parent)
        super.setLocalRotation(Quaternion.axisAngle(Vector3(0f, 1f, 0f), rotation))
        super.setLocalPosition(positionVector)
        super.setRenderable(modelRenderable)
    }
}
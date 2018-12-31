package artictactoe.dataModels

import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Renderable

/**
 * Created by Iván Carrasco on 31/12/2018.
 */
abstract class BaseNode(
    val nodeType: NodeType,
    val nodeId: Int,
    val renderable: Renderable
) {
    enum class NodeType {
        PARENT, CIRCLE, X
    }

    lateinit var position: Vector3
}
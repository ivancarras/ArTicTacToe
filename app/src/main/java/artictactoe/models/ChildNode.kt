package artictactoe.models

import com.google.ar.sceneform.rendering.Renderable

/**
 * Created by Iván Carrasco on 31/12/2018.
 */
class ChildNode(
    nodeType: NodeType,
    nodeId: Int,
    renderable: Renderable
) : BaseNode(nodeType, nodeId, renderable) {

}
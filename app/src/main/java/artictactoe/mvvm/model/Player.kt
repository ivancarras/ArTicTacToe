package artictactoe.mvvm.model

/**
 * Created by Wembley Studios on 06/01/2019.
 */
data class Player(val userName: String, val nodeType: BaseNode.NodeType) {
    constructor() : this("", BaseNode.NodeType.CIRCLE)
}
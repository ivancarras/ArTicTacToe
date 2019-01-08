package artictactoe.mvvm.model

/**
 * Created by Wembley Studios on 06/01/2019.
 */
data class Player(val playerId: Int, val name: String, val nodeType:BaseNode.NodeType,val gamesPlayed: Int)
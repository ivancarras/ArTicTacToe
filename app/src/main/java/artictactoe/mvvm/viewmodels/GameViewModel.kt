package artictactoe.mvvm.viewmodels

import android.arch.lifecycle.ViewModel
import artictactoe.mvvm.model.BaseNode
import artictactoe.mvvm.model.Game
import artictactoe.mvvm.model.Player

/**
 * Created by Iván Carrasco on 02/01/2019.
 */
class GameViewModel(player1: String, player2: String) : ViewModel() {
    private val game: Game by lazy {
        //we need repository
        Game(
            0,
            Player(0, "Xasd", BaseNode.NodeType.CIRCLE, 1),
            Player(1, "afas", BaseNode.NodeType.X, 2)
        )
    }
    private val cells:ObservableArrayMap<String,String> by lazy{

    }
}
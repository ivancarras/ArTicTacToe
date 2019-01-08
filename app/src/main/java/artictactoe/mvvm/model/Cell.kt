package artictactoe.mvvm.model

/**
 * Created by Iván Carrasco on 06/01/2019.
 */
data class Cell(val player: Player?) {
    fun isEmpty() =
        this.player == null
}
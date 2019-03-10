package artictactoe.mvvm.repository

import artictactoe.mvvm.model.Cell
import artictactoe.mvvm.model.Game
import artictactoe.mvvm.model.Player
import io.reactivex.Single

/**
 * Created by Iván Carrasco Alonso on 10/03/2019.
 */
interface IFirebaseRepository {
    fun setCells(cells: List<List<Cell>>, gameID: Int): Single<List<List<Cell>>>
    fun createGameRoom(): Single<Game>
    fun addCloudAnchorID(cloudAnchorID: String, gameID: Int): Single<Game>
    fun getGameRoomByID(gameID: String): Single<Game>
    fun introPlayerData(userName: String, gameID: Int, playerOrder: String): Single<Player>
    fun switchCurrentPlayer(player: Player, gameID: Int): Single<Player>
}
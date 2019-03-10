package artictactoe.mvvm.repository

import artictactoe.mvvm.model.Player
import io.reactivex.Single

interface IPlayerRepository {
    fun introPlayerData(userName: String, gameID: Int, playerOrder: String): Single<Player>
    fun switchCurrentPlayer(player: Player, gameID: Int): Single<Player>
}
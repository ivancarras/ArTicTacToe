package artictactoe.mvvm.repository

import artictactoe.mvvm.model.Game
import io.reactivex.Single

/**
 * Created by Iván Carrasco Alonso on 10/03/2019.
 */
interface IGameRepository {
    fun createGameRoom(): Single<Game>
    fun addCloudAnchorID(cloudAnchorID: String, gameID: Int): Single<Game>
    fun getGameRoomByID(gameID: Int): Single<Game>
}
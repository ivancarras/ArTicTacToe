package artictactoe.mvvm.repository

import artictactoe.mvvm.model.Cell
import artictactoe.mvvm.model.Game
import artictactoe.mvvm.model.Player
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Created by Iván Carrasco Alonso on 13/01/2019.
 */
class Repository : IRepository {

    val firebaseRepository: FirebaseRepository by lazy {
        FirebaseRepository()
    }

    override fun setCells(cells: List<List<Cell>>, gameID: Int): Single<List<List<Cell>>> =
        firebaseRepository.setCells(cells, gameID)

    override fun notifyCellChanges(gameID: Int): Observable<List<List<Cell>>> =
        firebaseRepository.notifyCellChanges(gameID)

    override fun createGameRoom(): Single<Game> =
        firebaseRepository.createGameRoom()

    override fun addCloudAnchorID(cloudAnchorID: String, gameID: Int): Single<Game> =
        firebaseRepository.addCloudAnchorID(cloudAnchorID, gameID)

    override fun getGameRoomByID(gameID: Int): Single<Game> =
        firebaseRepository.getGameRoomByID(gameID)

    override fun introPlayerData(userName: String, gameID: Int, playerOrder: String): Single<Player> =
        firebaseRepository.introPlayerData(userName, gameID, playerOrder)

    override fun switchCurrentPlayer(player: Player, gameID: Int): Single<Player> =
        firebaseRepository.switchCurrentPlayer(player, gameID)
}
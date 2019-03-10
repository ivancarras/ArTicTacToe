package artictactoe.mvvm.repository

import artictactoe.mvvm.model.Cell
import artictactoe.mvvm.model.Game
import artictactoe.mvvm.model.Player
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.Single
import io.reactivex.SingleEmitter

/**
 * Created by Iván Carrasco Alonso on 10/03/2019.
 */
class FirebaseRepository : IFirebaseRepository {

    val firebaseInstance by lazy {
        FirebaseDatabase.getInstance()
    }

    val gameRepository: IGameRepository by lazy {
        GameRepository(firebaseInstance)
    }

    val cellRepository: ICellRepository by lazy {
        CellRepository(firebaseInstance)
    }

    val playerRepository: IPlayerRepository by lazy {
        PlayerRepository(firebaseInstance)
    }

    override fun setCells(cells: List<List<Cell>>, gameID: Int): Single<List<List<Cell>>> =
        cellRepository.setCells(cells, gameID)


    override fun createGameRoom(): Single<Game> =
        gameRepository.createGameRoom()

    override fun addCloudAnchorID(cloudAnchorID: String, gameID: Int): Single<Game> =
        gameRepository.addCloudAnchorID(cloudAnchorID, gameID)


    override fun getGameRoomByID(gameID: String): Single<Game> =
        gameRepository.getGameRoomByID(gameID)

    override fun introPlayerData(userName: String, gameID: Int, playerOrder: String): Single<Player> =
        playerRepository.introPlayerData(userName, gameID, playerOrder)

    override fun switchCurrentPlayer(player: Player, gameID: Int): Single<Player> =
        playerRepository.switchCurrentPlayer(player, gameID)

    override fun requestSwitchCurrentPlayer(player: Player, gameID: Int, emitter: SingleEmitter<Player>) =
        playerRepository.requestSwitchCurrentPlayer(player, gameID, emitter)

    companion object {
        const val KEY_ROOT_DIR_GAMES = "games_data"
    }
}
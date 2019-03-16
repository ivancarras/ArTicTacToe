package artictactoe.mvvm.repository

import artictactoe.mvvm.model.BaseNode
import artictactoe.mvvm.model.Player
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.schedulers.Schedulers

/**
 * Created by Iván Carrasco Alonso on 10/03/2019.
 */
class PlayerRepository(val firebaseInstance: FirebaseDatabase) : IPlayerRepository {
    override fun introPlayerData(userName: String, gameID: Int, playerOrder: String): Single<Player> {
        return Single.create<Player> { emitter ->
            requestIntroPlayerData(userName, gameID, playerOrder, emitter)
        }.subscribeOn(Schedulers.single())
    }

    override fun switchCurrentPlayer(player: Player, gameID: Int): Single<Player> {
        return Single.create<Player> { emitter ->
            requestSwitchCurrentPlayer(player, gameID, emitter)
        }.subscribeOn(Schedulers.single())
    }

    private fun requestSwitchCurrentPlayer(player: Player, gameID: Int, emitter: SingleEmitter<Player>) {
        firebaseInstance
            .getReference(FirebaseRepository.KEY_ROOT_DIR_GAMES)
            .child(gameID.toString())
            .child("currentPlayer")
            .setValue(player)
            .addOnSuccessListener {
                emitter.onSuccess(player)
            }

    }

    private fun requestIntroPlayerData(
        userName: String,
        gameID: Int,
        playerOrder: String,
        emitter: SingleEmitter<Player>
    ) {
        val player = if (playerOrder == "player1") Player(
            userName,
            BaseNode.NodeType.X
        ) else Player(userName, BaseNode.NodeType.CIRCLE)

        firebaseInstance
            .getReference(FirebaseRepository.KEY_ROOT_DIR_GAMES)
            .child(gameID.toString())
            .child(playerOrder)
            .setValue(player)
            .addOnSuccessListener {
                emitter.onSuccess(player)
            }
    }
}
package artictactoe.mvvm.repository

import android.util.Log
import artictactoe.managers.StoreManager
import artictactoe.mvvm.model.Game
import com.google.firebase.database.*
import io.reactivex.Single
import io.reactivex.SingleEmitter

/**
 * Created by Iván Carrasco Alonso on 10/03/2019.
 */
class GameRepository(val firebaseInstance: FirebaseDatabase) : IGameRepository {

    override fun createGameRoom(): Single<Game> {
        //The call method?
        return Single.create<Game> { emitter ->
            requestCreateRoom(emitter)
        }
    }

    override fun addCloudAnchorID(cloudAnchorID: String, gameID: Int): Single<Game> {
        return Single.create { emitter ->
            requestSetCloudAnchorID(cloudAnchorID, gameID, emitter)
        }
    }

    override fun getGameRoomByID(gameID: String): Single<Game> {
        return Single.create { emitter ->
            requestGameRoomByID(gameID, emitter)
        }
    }

    private fun requestCreateRoom(emitter: SingleEmitter<Game>) {
        firebaseInstance
            .getReference(FirebaseRepository.KEY_ROOT_DIR_GAMES)
            .child(GameRepository.KEY_NEXT_GAME_ID)
            .runTransaction(object : Transaction.Handler {
                override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                    if (!p1) {
                        Log.e(StoreManager.TAG, "Firebase Error", p0?.toException())
                    } else {
                        p2?.value?.let {
                            val gameId: Int = (it as Long).toInt()
                            insertInitialGameData(gameId, emitter)
                        }
                    }
                }

                override fun doTransaction(p0: MutableData): Transaction.Result {
                    var gameId: Int? = (p0.value as? Long)?.toInt()
                    if (gameId == null) {
                        gameId = GameRepository.INITIAL_GAME_ID
                    }
                    p0.value = gameId + 1
                    return Transaction.success(p0)
                }
            })
    }

    private fun insertInitialGameData(gameID: Int, emitter: SingleEmitter<Game>) {
        val newGame = Game(gameID)
        firebaseInstance
            .getReference(FirebaseRepository.KEY_ROOT_DIR_GAMES)
            .child(gameID.toString())
            .setValue(newGame)
            .addOnCompleteListener {
                emitter.onSuccess(newGame)
            }
    }

    private fun requestSetCloudAnchorID(
        cloudAnchorID: String,
        gameID: Int,
        emitter: SingleEmitter<Game>
    ) {
        firebaseInstance
            .getReference(FirebaseRepository.KEY_ROOT_DIR_GAMES)
            .child(gameID.toString())
            .child("cloudAnchorId")
            .setValue(cloudAnchorID)
            .addOnSuccessListener {
                emitter.onSuccess(Game())
            }
    }

    private fun requestGameRoomByID(gameID: String, emitter: SingleEmitter<Game>) {
        firebaseInstance
            .getReference(FirebaseRepository.KEY_ROOT_DIR_GAMES)
            .child(gameID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    p0.getValue(Game::class.java)?.let {
                        emitter.onSuccess(it)
                    }
                }
            }
            )
    }

    companion object {
        const val KEY_NEXT_GAME_ID = "next_game_id"
        const val INITIAL_GAME_ID = 0
    }
}
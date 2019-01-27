package artictactoe.mvvm.repository

import android.util.Log
import artictactoe.managers.StoreManager
import artictactoe.mvvm.model.BaseNode
import artictactoe.mvvm.model.Cell
import artictactoe.mvvm.model.Game
import artictactoe.mvvm.model.Player
import com.google.firebase.database.*
import io.reactivex.Single
import io.reactivex.SingleEmitter

/**
 * Created by Iván Carrasco Alonso on 13/01/2019.
 */
class Repository {
    val firebaseInstance by lazy {
        FirebaseDatabase.getInstance()
    }
    //1. Create room-
    //1.1 Get gameID-
    //1.2 Add Cloud anchor ID associated to GameID
    //2. Get all Rooms
    //2.1 Select Room -> Intro gameID
    //2.2 Intro players data
    //2.3 Get Cells
    //2.4 Set Cells
    //2.5 Switch Current Player

    fun createGameRoom(): Single<Game> {
        //The call method?
        return Single.create<Game> { emitter ->
            requestCreateRoom(emitter)
        }
    }

    private fun requestCreateRoom(emitter: SingleEmitter<Game>) {
        firebaseInstance
            .getReference(KEY_ROOT_DIR_GAMES)
            .child(KEY_NEXT_GAME_ID)
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
                        gameId = INITIAL_GAME_ID
                    }
                    p0.value = gameId + 1
                    return Transaction.success(p0)
                }
            })
    }

    private fun insertInitialGameData(gameID: Int, emitter: SingleEmitter<Game>) {
        val newGame = Game(gameID)
        firebaseInstance
            .getReference(KEY_ROOT_DIR_GAMES)
            .child(gameID.toString())
            .setValue(newGame)
            .addOnCompleteListener {
                emitter.onSuccess(newGame)
            }
    }

    fun addCloudAnchorID(cloudAnchorID: String, gameID: Int): Single<Game> {
        return Single.create { emitter ->
            requestSetCloudAnchorID(cloudAnchorID, gameID, emitter)
        }
    }

    private fun requestSetCloudAnchorID(
        cloudAnchorID: String,
        gameID: Int,
        emitter: SingleEmitter<Game>
    ) {
        firebaseInstance
            .getReference(KEY_ROOT_DIR_GAMES)
            .child(gameID.toString())
            .child("cloudAnchorId")
            .setValue(cloudAnchorID)
            .addOnSuccessListener {
                emitter.onSuccess(Game())
            }
    }

    fun getGameRoomByID(gameID: String): Single<Game> {
        return Single.create { emitter ->
            requestGameRoomByID(gameID, emitter)
        }
    }

    private fun requestGameRoomByID(gameID: String, emitter: SingleEmitter<Game>) {
        firebaseInstance
            .getReference(KEY_ROOT_DIR_GAMES)
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

    fun introPlayerData(userName: String, gameID: Int, playerOrder: String): Single<Player> {
        return Single.create { emitter ->
            requestIntroPlayerData(userName, gameID, playerOrder, emitter)
        }
    }

    fun requestIntroPlayerData(
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
            .getReference(KEY_ROOT_DIR_GAMES)
            .child(gameID.toString())
            .child(playerOrder)
            .setValue(player)
            .addOnSuccessListener {
                emitter.onSuccess(player)
            }
    }

    fun setCells(cells: List<List<Cell>>, gameID: Int): Single<List<List<Cell>>> {
        return Single.create { emitter ->
            requestSetCells(cells, gameID, emitter)
        }
    }

    private fun requestSetCells(
        cells: List<List<Cell>>,
        gameID: Int,
        emitter: SingleEmitter<List<List<Cell>>>
    ) {
        firebaseInstance
            .getReference(KEY_ROOT_DIR_GAMES)
            .child(gameID.toString())
            .child("cells")
            .setValue(cells)
            .addOnSuccessListener {
                emitter.onSuccess(cells)
            }
    }

    fun switchCurrentPlayer(player: Player, gameID: Int): Single<Player> {
        return Single.create { emitter ->
            requestSwitchCurrentPlayer(player, gameID, emitter)
        }
    }

    fun requestSwitchCurrentPlayer(player: Player, gameID: Int, emitter: SingleEmitter<Player>) {

        firebaseInstance
            .getReference(KEY_ROOT_DIR_GAMES)
            .child(gameID.toString())
            .child("currentPlayer")
            .setValue(player)
            .addOnSuccessListener {
                emitter.onSuccess(player)
            }

    }


    companion object {
        const val KEY_ROOT_DIR_GAMES = "games_data"
        const val KEY_NEXT_GAME_ID = "next_game_id"
        const val INITIAL_GAME_ID = 0
    }
}
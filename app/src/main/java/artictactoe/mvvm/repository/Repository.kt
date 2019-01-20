package artictactoe.mvvm.repository

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import artictactoe.managers.StoreManager
import artictactoe.mvvm.model.BaseNode
import artictactoe.mvvm.model.Cell
import artictactoe.mvvm.model.Game
import artictactoe.mvvm.model.Player
import com.google.firebase.database.*


/**
 * Created by Iván Carrasco Alonso on 13/01/2019.
 */
class Repository(
    currentGameLiveData: MutableLiveData<Game>,
    val gameRoomsLiveData: MutableLiveData<List<Game>>
) : FirebaseRepositoryBase<Game>(currentGameLiveData) {

    //1. Create room-
    //1.1 Get gameID-
    //1.2 Add Cloud anchor ID associated to GameID
    //2. Get all Rooms
    //2.1 Select Room -> Intro gameID
    //2.2 Intro players data
    //2.3 Get Cells
    //2.4 Set Cells
    //2.5 Switch Current Player

    fun createGameRoom() {
        getGameId()
    }

    private fun getGameId() {
        firebaseInstance
            .getReference(KEY_ROOT_DIR)
            .child(KEY_NEXT_GAME_ID)
            .runTransaction(object : Transaction.Handler {
                override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                    if (!p1) {
                        Log.e(StoreManager.TAG, "Firebase Error", p0?.toException())
                    } else {
                        p2?.value?.let {
                            val gameId: Int = (it as Long).toInt()
                            insertInitialGameData(gameId)
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

    fun addCloudAnchorID(cloudAnchorID: String) {
        firebaseInstance
            .getReference(KEY_ROOT_DIR)
            .child(liveData.value?.gameID.toString())
            .child("cloudAnchorId")
            .setValue(cloudAnchorID)
            .addOnSuccessListener {
                liveData.value?.cloudAnchorId = cloudAnchorID.toInt()
            }
    }

    fun getGameRooms() {
        firebaseInstance
            .getReference(KEY_ROOT_DIR)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val gameRooms: MutableList<Game> = mutableListOf<Game>()
                    p0.children.forEach {
                        if (it.key != KEY_NEXT_GAME_ID) {
                            val gameToAdd = it.getValue(Game::class.java)
                            gameToAdd?.let {
                                gameRooms.add(gameToAdd)
                            }
                        }
                    }
                    gameRoomsLiveData.value = gameRooms
                }
            }
            )
    }

    fun getGameRoomByID(gameID: String, cloudAnchorCallback: (Int) -> Unit) {
        firebaseInstance
            .getReference(KEY_ROOT_DIR)
            .child(gameID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    liveData.value = p0.getValue(Game::class.java)
                    liveData.value?.cloudAnchorId?.let {
                        cloudAnchorCallback(it)
                    }
                }
            }
            )
    }

    private fun insertInitialGameData(gameID: Int) {
        val newGame = Game(gameID)
        firebaseInstance
            .getReference(KEY_ROOT_DIR)
            .child(gameID.toString())
            .setValue(newGame)
            .addOnCompleteListener {
                liveData.value = newGame
            }
    }

    fun introPlayerData(userName: String) {
        //Check if the table is not full
        if (!tableIsFull()) {
            if (liveData.value?.player1 == null) {
                val player1 = Player(userName, BaseNode.NodeType.X)

                firebaseInstance
                    .getReference(KEY_ROOT_DIR)
                    .child(liveData.value?.gameID.toString())
                    .child("player1")
                    .setValue(player1)
                    .addOnSuccessListener {
                        liveData.value?.currentPlayer = player1
                        liveData.value?.player1 = player1
                    }


            } else if (liveData.value?.player2 == null) {
                val player2 = Player(userName, BaseNode.NodeType.CIRCLE)
                firebaseInstance
                    .getReference(KEY_ROOT_DIR)
                    .child(liveData.value?.gameID.toString())
                    .child("player2")
                    .setValue(player2)
                    .addOnSuccessListener {
                        liveData.value?.player2 = player2
                    }
            }
        }
    }

    private fun tableIsFull() =
        !(liveData.value?.player1 == null || liveData.value?.player2 == null)

    fun setCells(cells: List<List<Cell>>) {
        firebaseInstance
            .getReference(KEY_ROOT_DIR)
            .child(liveData.value?.gameID.toString())
            .child("cells")
            .setValue(cells)
            .addOnSuccessListener {
                liveData.value?.cells = cells
            }
    }

    fun switchCurrentPlayer() {
        val currentPlayer = getDistinctPlayer()
        if (currentPlayer != null) {
            firebaseInstance
                .getReference(KEY_ROOT_DIR)
                .child(liveData.value?.gameID.toString())
                .child("currentPlayer")
                .setValue(currentPlayer)
                .addOnSuccessListener {
                    liveData.value?.currentPlayer = currentPlayer
                }
        }
    }

    private fun getDistinctPlayer() =
        if (liveData.value?.currentPlayer == liveData.value?.player1) liveData.value?.player2
        else liveData.value?.player1


    companion object {
        const val KEY_ROOT_DIR = "ar_tic_tac_toe"
        const val KEY_NEXT_GAME_ID = "next_game_id"
        const val INITIAL_GAME_ID = 0
    }
}
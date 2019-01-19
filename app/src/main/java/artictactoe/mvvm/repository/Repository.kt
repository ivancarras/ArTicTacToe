package artictactoe.mvvm.repository

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import artictactoe.managers.StoreManager
import artictactoe.mvvm.model.Game
import com.google.firebase.database.*

/**
 * Created by Iván Carrasco Alonso on 13/01/2019.
 */
class Repository(liveData: MutableLiveData<Game>) : FirebaseRepositoryBase<Game>(liveData) {

    init {
        receiveData()
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

    fun receiveData() {
        firebaseInstance
            .getReference(KEY_ROOT_DIR)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    liveData.value = p0.getValue(Game::class.java)
                }
            })
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


    companion object {
        const val KEY_ROOT_DIR = "ar_tic_tac_toe"
        const val KEY_NEXT_GAME_ID = "next_game_id"
        const val INITIAL_GAME_ID = 0
    }
}
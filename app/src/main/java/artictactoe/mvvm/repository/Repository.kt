package artictactoe.mvvm.repository

import android.arch.lifecycle.MutableLiveData
import artictactoe.mvvm.model.BaseNode
import artictactoe.mvvm.model.Game
import artictactoe.mvvm.model.Player
import com.google.firebase.database.*

/**
 * Created by Iván Carrasco Alonso on 13/01/2019.
 */
class Repository {
    val gameInfoLiveData: MutableLiveData<Game> = MutableLiveData()
    private lateinit var rootRef: DatabaseReference
    var game: Game = Game(
        0,
        Player(0, "Iván", BaseNode.NodeType.CIRCLE),
        Player(1, "Sergio", BaseNode.NodeType.X)
    )


    /*fun updateGame(game: Game) {
        //Enviar new game
        //recibir callback
        //Actualizar this.game
        //setValue gameInfoLiveData
    }*/
    init {
        FirebaseDatabase.getInstance()
            .getReference(KEY_ROOT_DIR)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    gameInfoLiveData.value = Game(
                        1,
                        Player(2, "adsad", BaseNode.NodeType.X),
                        Player(2, "adsad", BaseNode.NodeType.CIRCLE)
                    )
                }
            })
    }

    companion object {
        const val KEY_ROOT_DIR = "ar_tic_tac_toe"
    }
}
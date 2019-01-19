package artictactoe.mvvm.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import artictactoe.handlers.ArHandler
import artictactoe.mvvm.model.Game
import artictactoe.mvvm.repository.Repository
import artictactoe.mvvm.view.CustomArFragment
import com.google.ar.core.Anchor


/**
 * Created by Iván Carrasco on 02/01/2019.
 */
class GameViewModel : ViewModel() {
    val liveData: MutableLiveData<Game> = MutableLiveData()
    val arHandler by lazy {
        ArHandler()
    }
    private val repository by lazy {
        Repository(liveData)
    }

    fun createGameRoom() {
        repository.createGameRoom()
    }

    fun createCloudAnchor(customArFragment: CustomArFragment, anchor: Anchor) {
        arHandler.createCloudAnchor(customArFragment, anchor) { cloudID ->
            repository.addCloudAnchorID(cloudID)
        }
    }
    //1. Create room

    //2. Get all Rooms
    //2.1 Select Room -> Intro gameID
    //2.2 Intro players data
    //2.3 Get Cells
    //2.4 Set Cells
    //2.5 Switch Current Player


}
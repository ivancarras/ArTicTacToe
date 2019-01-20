package artictactoe.mvvm.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import artictactoe.handlers.ArHandler
import artictactoe.mvvm.model.Cell
import artictactoe.mvvm.model.Game
import artictactoe.mvvm.repository.Repository
import artictactoe.mvvm.view.CustomArFragment
import com.google.ar.core.Anchor


/**
 * Created by Iván Carrasco on 02/01/2019.
 */
class GameViewModel : ViewModel() {
    val currentGameLiveData: MutableLiveData<Game> = MutableLiveData()
    val gameRoomsLiveData: MutableLiveData<List<Game>> = MutableLiveData()
    private val arHandler by lazy {
        ArHandler()
    }
    private val repository by lazy {
        Repository(currentGameLiveData, gameRoomsLiveData)
    }

    init {
        repository.createGameRoom()
        introPlayerData("iván")
        //getGameRoomById("4")
    }

    fun createGameRoom() {
        repository.createGameRoom()
    }

    fun createCloudAnchor(customArFragment: CustomArFragment, anchor: Anchor) {
        arHandler.createCloudAnchor(customArFragment, anchor) { cloudID ->
            repository.addCloudAnchorID(cloudID)
        }
    }

    fun getGameRoomById(gameID: String, customArFragment: CustomArFragment) {
        //we resolve cloud anchor in the callback of the getCameRoomById
        repository.getGameRoomByID(gameID) {
            resolveCloudAnchor(it, customArFragment)
            //Now we have to draw the 3D elements refered to this anchor for that we use the data model
            //IMPORTANT
        }
    }

    fun introPlayerData(userName: String) {
        repository.introPlayerData(userName)
    }

    fun setCells(cells: List<List<Cell>>) {
        repository.setCells(cells)
    }

    fun switchCurrentPlayer() {
        repository.switchCurrentPlayer()
    }

    fun resolveCloudAnchor(cloudAnchorID: Int, customArFragment: CustomArFragment) {
        //We Get the resolve anchor
        arHandler.resolveAnchor(cloudAnchorID.toString(), customArFragment)
    }

    fun update3DScene(){
        //We have to update the scene refered to the live data model, we can use the observer in the view for call this function
    }
}
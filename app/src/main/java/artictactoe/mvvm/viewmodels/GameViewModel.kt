package artictactoe.mvvm.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import artictactoe.handlers.ArHandler
import artictactoe.managers.DisposableManager
import artictactoe.mvvm.model.BaseNode
import artictactoe.mvvm.model.Cell
import artictactoe.mvvm.model.Game
import artictactoe.mvvm.repository.IRepository
import artictactoe.mvvm.repository.Repository
import artictactoe.mvvm.view.CustomArFragment
import com.google.ar.core.Anchor
import io.reactivex.disposables.Disposable

/**
 * Created by Iván Carrasco on 02/01/2019.
 */
class GameViewModel : ViewModel() {

    val currentGameLiveData by lazy {
        MutableLiveData<Game>()
    }
    private val arHandler by lazy {
        ArHandler()
    }
    private val repository: IRepository by lazy {
        Repository()
    }
    private val disposableManager by lazy {
        DisposableManager.instance
    }

    override fun onCleared() {
        disposableManager.clear()
        super.onCleared()
    }

    fun createGameRoom() {
        val disposable: Disposable = repository.createGameRoom().subscribe { game ->
            currentGameLiveData.value = game
        }
        disposableManager.add(disposable)
    }

    fun createCloudAnchor(customArFragment: CustomArFragment, anchor: Anchor) {
        arHandler.createCloudAnchor(customArFragment, anchor) { cloudID ->
            currentGameLiveData.value?.let {
                val disposable: Disposable =
                    repository.addCloudAnchorID(cloudID, it.gameID).subscribe { it ->
                        currentGameLiveData.value =
                            currentGameLiveData.value?.copy(cloudAnchorId = cloudID.toInt())
                    }
                disposableManager.add(disposable)
            }
        }
    }

    fun getGameRoomById(gameID: String, customArFragment: CustomArFragment) {
        //we resolve cloud anchor in the callback of the getCameRoomById
        val disposable: Disposable = repository.getGameRoomByID(gameID).subscribe { it ->
            currentGameLiveData.value = it
            it.cloudAnchorId?.let { cloudID ->
                resolveCloudAnchor(cloudID, customArFragment)
            }
        }
        disposableManager.add(disposable)
    }

    fun introPlayerData(userName: String) {
        if (!tableIsFull()) {
            val playerOrder: String = if (currentGameLiveData.value?.player1 == null) "player1" else
                "player2"

            currentGameLiveData.value?.gameID?.let { gameID ->
                val disposable =
                    repository.introPlayerData(userName, gameID, playerOrder).subscribe { player ->
                        if (player.nodeType == BaseNode.NodeType.X) {
                            currentGameLiveData.value =
                                currentGameLiveData.value?.copy(player1 = player)
                        } else {
                            currentGameLiveData.value =
                                currentGameLiveData.value?.copy(player2 = player)
                        }
                    }
                disposableManager.add(disposable)
            }
        }
    }

    fun setCells(cells: List<List<Cell>>) {
        currentGameLiveData.value?.gameID?.let {
            val disposable = repository.setCells(cells, it).subscribe { newCells ->
                currentGameLiveData.value = currentGameLiveData.value?.copy(cells = newCells)
            }
            disposableManager.add(disposable)
        }
    }

    fun switchCurrentPlayer() {
        val currentPlayer = getDistinctPlayer()
        currentGameLiveData.value?.gameID?.let { gameID ->
            currentPlayer?.let {
                repository.switchCurrentPlayer(currentPlayer, gameID)
            }
        }

    }

    private fun resolveCloudAnchor(cloudAnchorID: Int, customArFragment: CustomArFragment) {
        //We Get the resolve anchor
        arHandler.resolveAnchor(cloudAnchorID.toString(), customArFragment)
    }

    fun update3DScene() {
        //We have to update the scene refered to the live data model, we can use the observer in the view for call this function
    }

    private fun tableIsFull() =
        !(currentGameLiveData.value?.player1 == null || currentGameLiveData.value?.player2 == null)

    private fun getDistinctPlayer() =
        if (currentGameLiveData.value?.currentPlayer == currentGameLiveData.value?.player1) currentGameLiveData.value?.player2
        else currentGameLiveData.value?.player1
}
package artictactoe.mvvm.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import artictactoe.handlers.ArHandler
import artictactoe.handlers.IArHandler
import artictactoe.managers.DisposableManager
import artictactoe.mvvm.model.BaseNode
import artictactoe.mvvm.model.Cell
import artictactoe.mvvm.model.Game
import artictactoe.mvvm.repository.IRepository
import artictactoe.mvvm.repository.Repository
import artictactoe.mvvm.view.CustomArFragment
import com.google.ar.core.Anchor
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * Created by Iván Carrasco on 02/01/2019.
 */
class TicTacToeViewModel : ViewModel(), ITicTacToeViewModel {

    val currentGameLiveData by lazy {
        MutableLiveData<Game>()
    }

    private val arHandler: IArHandler by lazy {
        ArHandler()
    }
    private val repository: IRepository by lazy {
        Repository()
    }

    override fun onCleared() {
        DisposableManager.clear()
        super.onCleared()
    }

    override fun createGameRoom(): Single<Game> =
        Single.create<Game> { emitter ->
            DisposableManager.add {
                repository.createGameRoom().subscribe { game ->
                    currentGameLiveData.value = game
                    emitter.onSuccess(game)
                }
            }
        }.subscribeOn(Schedulers.single())


    override fun getGameRoomById(gameID: Int, customArFragment: CustomArFragment): Single<Game> =
        Single.create<Game> { emitter ->
            //we resolve cloud anchor in the callback of the getCameRoomById
            DisposableManager.add {
                repository.getGameRoomByID(gameID).subscribe { it ->
                    currentGameLiveData.value = it
                    emitter.onSuccess(it)

                    it.cloudAnchorId?.let { cloudID ->
                        resolveCloudAnchor(cloudID, customArFragment)
                    }

                }
            }
        }.subscribeOn(Schedulers.single())


    override fun createCloudAnchor(customArFragment: CustomArFragment, anchor: Anchor): Single<Game> =
        Single.create<Game> { emitter ->
            DisposableManager.add {
                arHandler.createCloudAnchor(customArFragment, anchor).subscribe { cloudID ->
                    currentGameLiveData.value?.let {
                        DisposableManager.add {
                            repository.addCloudAnchorID(cloudID, it.gameID).subscribe { it ->
                                currentGameLiveData.value =
                                    currentGameLiveData.value?.copy(cloudAnchorId = it.cloudAnchorId)
                                emitter.onSuccess(it)
                            }
                        }
                    }
                }
            }
        }.subscribeOn(Schedulers.single())

    override fun introPlayerData(userName: String): Single<Game> =
        Single.create<Game> { emitter ->
            if (!tableIsFull()) {
                val playerOrder: String = if (currentGameLiveData.value?.player1 == null) "player1" else
                    "player2"

                currentGameLiveData.value?.gameID?.let { gameID ->
                    DisposableManager.add {
                        repository.introPlayerData(userName, gameID, playerOrder).subscribe { player ->
                            if (player.nodeType == BaseNode.NodeType.X) {
                                currentGameLiveData.value =
                                    currentGameLiveData.value?.copy(player1 = player)
                            } else {
                                currentGameLiveData.value =
                                    currentGameLiveData.value?.copy(player2 = player)
                            }
                            currentGameLiveData.value?.let {
                                emitter.onSuccess(it)
                            }

                        }
                    }
                }
            }
        }.subscribeOn(Schedulers.single())


    fun switchCurrentPlayer() {
        val currentPlayer = getDistinctPlayer()
        currentGameLiveData.value?.gameID?.let { gameID ->
            currentPlayer?.let {
                repository.switchCurrentPlayer(currentPlayer, gameID)
            }
        }
    }

    fun setCells(cells: List<List<Cell>>) {
        currentGameLiveData.value?.gameID?.let {
            DisposableManager.add {
                repository.setCells(cells, it).subscribe { newCells ->
                    currentGameLiveData.value = currentGameLiveData.value?.copy(cells = newCells)
                }
            }
        }
    }

    fun update3DScene() {
        //We have to update the scene refered to the live data model, we can use the observer in the view for call this function
    }

    private fun resolveCloudAnchor(cloudAnchorID: Int, customArFragment: CustomArFragment) {
        //We Get the resolve anchor
        arHandler.resolveCloudAnchor(cloudAnchorID.toString(), customArFragment)
    }

    private fun tableIsFull() =
        !(currentGameLiveData.value?.player1 == null || currentGameLiveData.value?.player2 == null)

    private fun getDistinctPlayer() =
        if (currentGameLiveData.value?.currentPlayer == currentGameLiveData.value?.player1) currentGameLiveData.value?.player2
        else currentGameLiveData.value?.player1
}
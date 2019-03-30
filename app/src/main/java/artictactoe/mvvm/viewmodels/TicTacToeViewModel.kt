package artictactoe.mvvm.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import artictactoe.handlers.ArHandler
import artictactoe.handlers.IArHandler
import artictactoe.mvvm.managers.DisposableManager
import artictactoe.mvvm.model.BaseNode
import artictactoe.mvvm.model.Cell
import artictactoe.mvvm.model.Game
import artictactoe.mvvm.repository.IRepository
import artictactoe.mvvm.repository.Repository
import artictactoe.mvvm.utils.subscribeAddingDisposable
import artictactoe.mvvm.view.CustomArFragment
import com.google.ar.core.Anchor
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * Created by Iván Carrasco on 02/01/2019.
 */
class TicTacToeViewModel : ViewModel(), ITicTacToeViewModel {

    private val currentGameLiveData by lazy {
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

            repository.createGameRoom().subscribeAddingDisposable { game ->
                currentGameLiveData.value = game
                emitter.onSuccess(game)
            }

        }.subscribeOn(Schedulers.single())


    override fun getGameRoomById(gameID: Int, customArFragment: CustomArFragment): Single<Game> =
        Single.create<Game> { emitter ->
            //we resolve cloud anchor in the callback of the getCameRoomById
            repository.getGameRoomByID(gameID).subscribeAddingDisposable {
                currentGameLiveData.value = it
                emitter.onSuccess(it)

                it.cloudAnchorId?.let { cloudID ->
                    resolveCloudAnchor(cloudID, customArFragment)
                }

            }
        }.subscribeOn(Schedulers.single())


    override fun createCloudAnchor(customArFragment: CustomArFragment, anchor: Anchor): Single<Game> =
        Single.create<Game> { emitter ->
            arHandler.createCloudAnchor(customArFragment, anchor).subscribeAddingDisposable { cloudID ->
                currentGameLiveData.value?.let {
                    repository.addCloudAnchorID(cloudID, it.gameID).subscribeAddingDisposable {
                        currentGameLiveData.value =
                            currentGameLiveData.value?.copy(cloudAnchorId = it.cloudAnchorId)
                        emitter.onSuccess(it)
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
                    repository.introPlayerData(userName, gameID, playerOrder).subscribeAddingDisposable { player ->
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
            repository.setCells(cells, it).subscribeAddingDisposable { newCells ->
                currentGameLiveData.value = currentGameLiveData.value?.copy(cells = newCells)
            }
        }
    }

    override fun notifyCellChanges(gameID: Int): Observable<List<List<Cell>>> =
        Observable.create { emitter ->
            repository.notifyCellChanges(gameID).subscribeAddingDisposable {
                //Aqui deberia haber un trigger dentro de un liveData wrapper
                // que indique que hay que actualizar el AR

                emitter.onNext(it)
            }
        }


    private fun resolveCloudAnchor(cloudAnchorID: String, customArFragment: CustomArFragment) {
        //We Get the resolve anchor
        arHandler.resolveCloudAnchor(cloudAnchorID, customArFragment)
    }

    private fun tableIsFull() =
        !(currentGameLiveData.value?.player1 == null || currentGameLiveData.value?.player2 == null)

    private fun getDistinctPlayer() =
        if (currentGameLiveData.value?.currentPlayer == currentGameLiveData.value?.player1) currentGameLiveData.value?.player2
        else currentGameLiveData.value?.player1
}
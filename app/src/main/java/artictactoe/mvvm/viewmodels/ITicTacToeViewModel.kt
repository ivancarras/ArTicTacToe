package artictactoe.mvvm.viewmodels

import artictactoe.mvvm.model.Game
import artictactoe.mvvm.view.CustomArFragment
import com.google.ar.core.Anchor
import io.reactivex.Single

/**
 * Created by Iván Carrasco Alonso on 10/03/2019.
 */
interface ITicTacToeViewModel {
    fun createGameRoom(): Single<Game>
    fun getGameRoomById(gameID: Int, customArFragment: CustomArFragment): Single<Game>
    fun createCloudAnchor(customArFragment: CustomArFragment, anchor: Anchor): Single<Game>
    fun introPlayerData(userName: String): Single<Game>
}
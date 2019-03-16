package artictactoe.mvvm.data

import android.arch.lifecycle.MutableLiveData
import artictactoe.mvvm.model.Game

/**
 * Created by Iván Carrasco Alonso on 10/03/2019.
 */
class TicTacToeDataProvider {
    val gameLiveData by lazy {
        MutableLiveData<Game>()
    }

    val trigger by lazy {
        LiveDataTrigger()
    }
}
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

    //When there is a update of the cells we use this to notify that we need to update de ar
    val arHandlerTrigger by lazy {
        LiveDataTrigger()
    }


}
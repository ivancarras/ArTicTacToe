package artictactoe.mvvm.viewmodels

import android.arch.lifecycle.ViewModel
import artictactoe.mvvm.repository.Repository


/**
 * Created by Iván Carrasco on 02/01/2019.
 */
class GameViewModel : ViewModel() {
    val repository: Repository by lazy {
        Repository()
    }

    init {

    }
}
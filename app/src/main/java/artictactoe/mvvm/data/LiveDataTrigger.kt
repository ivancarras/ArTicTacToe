package artictactoe.mvvm.data

import android.arch.lifecycle.LiveData

/**
 * Created by Iván Carrasco Alonso on 10/03/2019.
 */
class LiveDataTrigger : LiveData<Nothing>(), Triggeable {
    override fun Trigger() {
        this.value = null
    }
}
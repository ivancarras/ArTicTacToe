package artictactoe.mvvm.repository

import android.arch.lifecycle.MutableLiveData
import com.google.firebase.database.FirebaseDatabase

/**
 * Created by Iván Carrasco on 19/01/2019.
 */
abstract class FirebaseRepositoryBase<T>(val liveData: MutableLiveData<T>) {
    val firebaseInstance by lazy {
        FirebaseDatabase.getInstance()
    }
}
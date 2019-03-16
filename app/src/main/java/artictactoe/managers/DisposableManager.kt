package artictactoe.managers

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Iván Carrasco on 25/01/2019.
 */
object DisposableManager {
    private val compositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    fun add(disposable: () -> Disposable) {
        //Paque quede mas parsero pues asín con los corchetes
        compositeDisposable.add(disposable.invoke())
    }

    fun clear() {
        compositeDisposable.clear()
    }
}
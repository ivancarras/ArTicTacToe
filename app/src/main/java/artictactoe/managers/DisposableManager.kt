package artictactoe.managers

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Iván Carrasco on 25/01/2019.
 */
class DisposableManager private constructor() {
    private val compositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    fun add(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    fun clear() {
        compositeDisposable.clear()
    }

    companion object {
        val instance: DisposableManager by lazy {
            DisposableManager()
        }
    }
}
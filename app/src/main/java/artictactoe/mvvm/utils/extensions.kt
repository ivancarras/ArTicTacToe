package artictactoe.mvvm.utils

import android.support.v7.app.AppCompatActivity
import android.view.View
import artictactoe.mvvm.managers.DisposableManager
import io.reactivex.Observable
import io.reactivex.Single

fun <T : View> AppCompatActivity.findView(id: Int): Lazy<T> {
    return lazy {
        findViewById<T>(id)
    }
}

fun <T> Single<T>.subscribeAddingDisposable(onSuccess: (T) -> Unit) {
    DisposableManager.add {
        this.subscribe(onSuccess::invoke)
    }
}

fun <T> Observable<T>.subscribeAddingDisposable(onSuccess: (T) -> Unit) {
    DisposableManager.add {
        this.subscribe(onSuccess::invoke)
    }
}
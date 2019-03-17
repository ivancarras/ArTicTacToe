package artictactoe.mvvm.utils

import android.support.v7.app.AppCompatActivity
import android.view.View
import artictactoe.mvvm.managers.DisposableManager
import io.reactivex.Single
import io.reactivex.functions.Consumer

fun <T : View> AppCompatActivity.findView(id: Int): Lazy<T> {
    return lazy {
        findViewById(id) as T
    }
}

fun <T> Single<T>.subscribeAddingDisposable(onSuccess: Consumer<T>){
    DisposableManager.add {
        this.subscribe(onSuccess)
    }
}
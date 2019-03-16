package artictactoe.mvvm.utils

import android.support.v7.app.AppCompatActivity
import android.view.View

fun <T : View> AppCompatActivity.findView(id: Int): Lazy<T> {
    return lazy {
        findViewById(id) as T
    }
}

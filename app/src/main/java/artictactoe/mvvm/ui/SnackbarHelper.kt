package artictactoe.mvvm.ui

import android.app.Activity
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.view.View


/**
 * Created by Iván Carrasco Alonso on 23/12/2018.
 */
class SnackbarHelper {
    enum class DismissBehavior {
        HIDE, SHOW, FINISH
    }

    private var messageSnackbar: Snackbar? = null

    fun isShowing(): Boolean =
        messageSnackbar != null

    fun showMessage(activity: Activity, message: String) =
        show(activity, message, DismissBehavior.HIDE)

    fun hide(activity: Activity) {
        activity.runOnUiThread {
            messageSnackbar?.dismiss()
            messageSnackbar = null
        }
    }

    private fun show(
        activity: Activity, message: String, dismissBehavior: DismissBehavior
    ) {
        activity.runOnUiThread {
            messageSnackbar = Snackbar.make(
                activity.findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_INDEFINITE
            )
            messageSnackbar?.view?.setBackgroundColor(BACKGROUND_COLOR)
            if (dismissBehavior !== DismissBehavior.HIDE) {
                messageSnackbar?.setAction(
                    "Dismiss",
                    object:View.OnClickListener {
                        override fun onClick(v: View?) {
                            messageSnackbar?.dismiss()
                        }
                    })
                if (dismissBehavior === DismissBehavior.FINISH) {
                    messageSnackbar?.addCallback(
                        object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                super.onDismissed(transientBottomBar, event)
                                activity.finish()
                            }
                        })
                }
            }
            messageSnackbar?.show()
        }
    }

    companion object {
        const val BACKGROUND_COLOR = -0x40cdcdce
    }
}
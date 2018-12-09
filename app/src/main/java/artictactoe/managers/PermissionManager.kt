package artictactoe.managers

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.widget.Toast

/**
 * Created by Iván Carrasco on 02/12/2018.
 */

class PermissionManager private constructor() {
    private val permissionManagerFragment by lazy {
        PermissionManagerFragment()
    }
    private lateinit var successFunction: () -> Unit

    fun checkPermission(
        activity: FragmentActivity,
        permissionRequests: Array<String>?,
        requestCode: Int,
        successFunction: () -> Unit
    ) {
        this.successFunction = successFunction
        activity.supportFragmentManager.beginTransaction()
            .add(this.permissionManagerFragment, "")
            .commitNowAllowingStateLoss()
        activity.supportFragmentManager.executePendingTransactions()

        permissionRequests?.let {
            it.firstOrNull {
                ContextCompat.checkSelfPermission(
                    activity,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            }?.apply {
                permissionManagerFragment.requestPermissions(
                    permissionRequests,
                    requestCode
                )
            }
        }
        this.successFunction.invoke()
    }

    fun checkIsSupportedDeviceOrFinish(activity: Activity) {
        val openGlVersionString =
            (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .deviceConfigurationInfo
                .glEsVersion
        if (java.lang.Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                .show()
            activity.finish()
        }
    }

    companion object {
        class PermissionManagerFragment : Fragment() {
            override fun onRequestPermissionsResult(
                requestCode: Int,
                permissions: Array<out String>,
                grantResults: IntArray
            ) {
                grantResults.all { it == PERMISSION_GRANTED }.apply {
                    when (requestCode) {
                        INITIAL_PERMISSION -> {
                            instance.successFunction.invoke()
                        }
                    }
                }
            }

        }

        val instance by lazy { PermissionManager() }
        const val INITIAL_PERMISSION = 1
        const val MIN_OPENGL_VERSION = 3.0
    }

}
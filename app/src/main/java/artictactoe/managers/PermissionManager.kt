package artictactoe.managers

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast

/**
 * Created by Iván Carrasco on 02/12/2018.
 */

class PermissionManager private constructor() {

    fun checkPermission(
        activity: Activity,
        permissionRequests: Array<String>?,
        requestCode: Int

    ): Boolean {

        permissionRequests?.let {
            it.firstOrNull {
                ContextCompat.checkSelfPermission(
                    activity,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            }?.apply {
                ActivityCompat.requestPermissions(
                    activity,
                    permissionRequests,
                    requestCode
                )
                return false
            }
        }
        return true
    }

    fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG)
                .show()
            activity.finish()
            return false
        }
        val openGlVersionString =
            (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .deviceConfigurationInfo
                .glEsVersion
        if (java.lang.Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                .show()
            activity.finish()
            return false
        }
        return true
    }

    companion object {
        val instance by lazy { PermissionManager() }
        const val INITIAL_PERMISSION = 1
        const val MIN_OPENGL_VERSION = 3.0
    }
}
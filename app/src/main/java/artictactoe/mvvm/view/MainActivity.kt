package artictactoe.mvvm.view

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import artictactoe.managers.PermissionManager
import artictactoe.mvvm.utils.findView
import artictactoe.mvvm.viewmodels.GameViewModel
import tictactoe.R


/**
 * Created by Iván Carrasco Alonso on 02/12/2018.
 */
class MainActivity : AppCompatActivity() {
    private val customArFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as CustomArFragment
    }
    
   // private val textView: TextView by findView()
    private val snackbarHelper by lazy {
        SnackbarHelper()
    }

    val gameViewModel by lazy {
        ViewModelProviders.of(this).get(GameViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)
        gameViewModel.currentGameLiveData.observe(this, Observer {
            Log.i("MainActivity", "Game update")
            //Update UI
            //Update AR
        })

        gameViewModel.getGameRoomById("1", customArFragment)

        PermissionManager.instance.checkIsSupportedDeviceOrFinish(this)
        PermissionManager.instance.checkPermission(
            this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.INTERNET),
            PermissionManager.INITIAL_PERMISSION
        ) {
            snackbarHelper.showMessage(this, getString(R.string.permission_granted))
        }
    }
}
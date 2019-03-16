package artictactoe.mvvm.view

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import artictactoe.managers.DisposableManager
import artictactoe.managers.PermissionManager
import artictactoe.mvvm.viewmodels.ITicTacToeViewModel
import artictactoe.mvvm.viewmodels.TicTacToeViewModel
import tictactoe.R


/**
 * Created by Iván Carrasco Alonso on 02/12/2018.
 */
class MainActivity : AppCompatActivity() {
    private val customArFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as CustomArFragment
    }

    private val snackbarHelper by lazy {
        SnackbarHelper()
    }

    private val gameViewModel: ITicTacToeViewModel by lazy {
        ViewModelProviders.of(this).get(TicTacToeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        PermissionManager.instance.checkIsSupportedDeviceOrFinish(this)
        PermissionManager.instance.checkPermission(
            this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.INTERNET),
            PermissionManager.INITIAL_PERMISSION
        ) {
            snackbarHelper.showMessage(this, getString(R.string.permission_granted))
        }

        DisposableManager.add {
            gameViewModel.createGameRoom().subscribe { game ->
                game?.let {
                    snackbarHelper.showMessage(this, "Game insertado")
                }
            }
        }

        customArFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            DisposableManager.add {
                gameViewModel.createCloudAnchor(customArFragment, hitResult.createAnchor())
                    .subscribe { it ->
                        snackbarHelper.showMessage(this, "Cloud Ar hosteado")
                    }
            }
        }
        DisposableManager.add {
            gameViewModel.getGameRoomById(0, customArFragment).subscribe { it ->
                snackbarHelper.showMessage(this, "Conectado a la sala 0")
            }
        }

        DisposableManager.add {
            gameViewModel.introPlayerData("Iván").subscribe { it ->
                snackbarHelper.showMessage(this, "Nombre introducido")
            }
        }
    }
}
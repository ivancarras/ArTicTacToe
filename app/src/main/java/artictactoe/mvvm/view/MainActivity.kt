package artictactoe.mvvm.view

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import artictactoe.mvvm.managers.PermissionManager
import artictactoe.mvvm.utils.findView
import artictactoe.mvvm.utils.subscribeAddingDisposable
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

    private val btCreateRoom: Button by findView(R.id.createRoom)
    private val btConectRoom: Button by findView(R.id.conectRoom)
    private val etRoomID: EditText by findView(R.id.roomID)

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

        btCreateRoom.setOnClickListener {
            gameViewModel.createGameRoom().subscribeAddingDisposable { game ->
                game?.let {
                    snackbarHelper.showMessage(this, "Game insertado con gameID " + game.gameID)
                }
            }
        }

        customArFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            gameViewModel.createCloudAnchor(customArFragment, hitResult.createAnchor())
                .subscribeAddingDisposable { it ->
                    snackbarHelper.showMessage(this, "Cloud Ar hosteado para la room" + it.gameID)
                }
        }

        btConectRoom.setOnClickListener {
            if (etRoomID.text.toString().isNotEmpty()) {
                gameViewModel.getGameRoomById(etRoomID.text.toString().toInt(), customArFragment)
                    .subscribeAddingDisposable { it ->
                        snackbarHelper.showMessage(this, "Conectado a la sala " + it.gameID)
                    }
            }
        }
    }
    /**
    DisposableManager.add {
    gameViewModel.introPlayerData("Iván").subscribe { it ->
    snackbarHelper.showMessage(this, "Nombre introducido")
    }
    }**/
}

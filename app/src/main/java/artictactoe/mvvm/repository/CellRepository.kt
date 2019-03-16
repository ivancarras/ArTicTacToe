package artictactoe.mvvm.repository

import artictactoe.mvvm.model.Cell
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.schedulers.Schedulers

/**
 * Created by Iván Carrasco Alonso on 10/03/2019.
 */
class CellRepository(val firebaseInstance: FirebaseDatabase) : ICellRepository {

    override fun setCells(cells: List<List<Cell>>, gameID: Int): Single<List<List<Cell>>> {
        return Single.create<List<List<Cell>>> { emitter ->
            requestSetCells(cells, gameID, emitter)
        }.subscribeOn(Schedulers.single())
    }

    private fun requestSetCells(
        cells: List<List<Cell>>,
        gameID: Int,
        emitter: SingleEmitter<List<List<Cell>>>
    ) {
        firebaseInstance
            .getReference(FirebaseRepository.KEY_ROOT_DIR_GAMES)
            .child(gameID.toString())
            .child("cells")
            .setValue(cells)
            .addOnSuccessListener {
                emitter.onSuccess(cells)
            }
    }
}
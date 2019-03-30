package artictactoe.mvvm.repository

import artictactoe.mvvm.model.Cell
import com.google.firebase.database.*
import io.reactivex.Observable
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

    override fun notifyCellChanges(gameID: Int): Observable<List<List<Cell>>> =
        Observable.create { emitter ->
            firebaseInstance
                .getReference(FirebaseRepository.KEY_ROOT_DIR_GAMES)
                .child(gameID.toString())
                .child("cells")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        emitter.onError(Throwable("NotifyCellChanges error"))
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val cellsType = object : GenericTypeIndicator<ArrayList<ArrayList<@JvmSuppressWildcards Cell>>>() {}
                        p0.getValue(cellsType)?.let {
                            emitter.onNext(it)
                        }
                    }
                })
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
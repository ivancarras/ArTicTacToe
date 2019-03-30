package artictactoe.mvvm.repository

import artictactoe.mvvm.model.Cell
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Created by Iván Carrasco Alonso on 10/03/2019.
 */
interface ICellRepository {
    fun setCells(cells: List<List<Cell>>, gameID: Int): Single<List<List<Cell>>>
    fun notifyCellChanges(gameID: Int): Observable<List<List<Cell>>>
}
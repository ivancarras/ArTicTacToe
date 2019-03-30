package artictactoe.handlers

import artictactoe.mvvm.model.Cell
import artictactoe.mvvm.view.CustomArFragment
import com.google.ar.core.Anchor
import io.reactivex.Single

/**
 * Created by Iván Carrasco Alonso on 11/03/2019.
 */
interface IArHandler {

    fun createCloudAnchor(
        customArFragment: CustomArFragment,
        anchor: Anchor
    ): Single<String>

    fun resolveCloudAnchor(cloudAnchorID: String, customArFragment: CustomArFragment)

    fun redrawCells(cells: List<List<Cell>>)
}
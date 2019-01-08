package artictactoe.mvvm.model

import android.arch.lifecycle.MutableLiveData

/**
 * Created by Iván Carrasco on 06/01/2019.
 */
data class Game(val gameId: Int, val player1: Player, val player2: Player) {
    lateinit var currentPlayer: Player
    val winner: MutableLiveData<Player> = MutableLiveData()
    val cells by lazy {
        Array<Array<Cell>>(BOARD_SIZE) {
            Array<Cell>(BOARD_SIZE) {
                Cell(null)
            }
        }
    }

    init {
        currentPlayer = player1
    }

    fun horizontalLines(player: Player): Boolean {
        for (i in 0 until BOARD_SIZE) {
            if (cells[i][0].player == player && cells[i][1].player == player && cells[i][2].player == player) {
                return true
            }
        }
        return false
    }

    fun verticalLines(player: Player): Boolean {
        for (i in 0 until BOARD_SIZE) {
            if (cells[0][i].player == player && cells[1][i].player == player && cells[2][i].player == player) {
                return true
            }
        }
        return false
    }

    fun diagonalLines(player: Player): Boolean {
        var hasDiagonalLines = false

        for (i in 0 until BOARD_SIZE) {
            if (cells[i][i].player == player) {
                hasDiagonalLines = true
            } else {
                hasDiagonalLines = false
                break
            }
        }
        var k = 0
        if (!hasDiagonalLines) {
            for (i in BOARD_SIZE - 1 downTo 0) {
                if (cells[i+k][i].player == player) {
                    hasDiagonalLines = true
                } else {
                    hasDiagonalLines = false
                    break
                }
                k++
            }
        }
        return hasDiagonalLines
    }

    fun switchPlayer() {
        currentPlayer = if (currentPlayer == player1) player2 else player1
    }


    companion object {
        const val BOARD_SIZE = 3
    }
}
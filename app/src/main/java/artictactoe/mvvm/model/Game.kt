package artictactoe.mvvm.model

/**
 * Created by Iván Carrasco on 06/01/2019.
 */
data class Game(val gameId: Int, val player1: Player, val player2: Player) {
    lateinit var currentPlayer: Player
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

    fun horizontalLines(): Boolean {
        for (i in 0 until BOARD_SIZE) {
            if (cells[i][0].tokenNode?.player == currentPlayer && cells[i][1].tokenNode?.player == currentPlayer && cells[i][2].tokenNode?.player == currentPlayer) {
                return true
            }
        }
        return false
    }

    fun verticalLines(): Boolean {
        for (i in 0 until BOARD_SIZE) {
            if (cells[0][i].tokenNode?.player == currentPlayer && cells[1][i].tokenNode?.player == currentPlayer && cells[2][i].tokenNode?.player == currentPlayer) {
                return true
            }
        }
        return false
    }

    fun diagonalLines(): Boolean {
        var hasDiagonalLines = false

        for (i in 0 until BOARD_SIZE) {
            if (cells[i][i].tokenNode?.player == currentPlayer) {
                hasDiagonalLines = true
            } else {
                hasDiagonalLines = false
                break
            }
        }
        var k = 0
        if (!hasDiagonalLines) {
            for (i in BOARD_SIZE - 1 downTo 0) {
                if (cells[i + k][i].tokenNode?.player == currentPlayer) {
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

    fun hasGameEnded(): Boolean {
        return verticalLines() || horizontalLines() || diagonalLines()
    }

    companion object {
        const val BOARD_SIZE = 3
    }
}
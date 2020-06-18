package model

import kotlin.math.max
import kotlin.math.min

const val BOARD_SIZE = 10

const val WIN_LINE_LENGTH = 7

class GameBoard(private val users: List<UserInGame>){


    var lastMovedUser = UserInGame(-1, 0, false)
        private set

    var lastMovedTime: Long = 0
        private set

    private val board = List(BOARD_SIZE){
        MutableList<UserInGame?>(BOARD_SIZE) { null }
    }

    fun makeMove(x: Int, y:Int, user: UserInGame){
        board[x][y] = user
        lastMovedUser = user
        lastMovedTime = nowUtcMills()
    }

    fun checkIfCanMove(userId: Int, x: Int, y: Int){
        if(lastMovedUser.id == userId) userFault("Trying to make move several times in a row. Wait your turn.")
        if(x !in 0 until BOARD_SIZE || y !in 0 until BOARD_SIZE) userFault(
                "x=$x or y=$y are out of board. Board size=$BOARD_SIZE."
        )
        if(board[x][y] != null && board[x][y]!!.id != userId)
            userFault("Field ($x,$y) has already been occupied. Try to move to another field.")
    }

    fun isUserAlreadyOccupiedField(userId: Int, x: Int, y: Int) =
            board[x][y]?.id == userId

    fun snapshot() = board.map{it.toMutableList()}

    fun isDraw():Boolean{

        val userIdByIndex = users.map { it.id }
        val counts = userIdByIndex.map { 0 }.toMutableList()

        // horizontal
        for(x in 0 until BOARD_SIZE){
            counts.replaceAll { 0 }
            for(y in 0 until BOARD_SIZE){
                if(countNext(board[x][y], counts, userIdByIndex))
                    return false
            }
        }

        // vertical
        for(y in 0 until BOARD_SIZE){
            counts.replaceAll { 0 }
            for(x in 0 until BOARD_SIZE){
                if(countNext(board[x][y], counts, userIdByIndex))
                    return false
            }
        }

        val halfDiagonalsCount = BOARD_SIZE - WIN_LINE_LENGTH
        val availableDiagonalsCount = halfDiagonalsCount * 2 + 1

        // diagonal left to right
        for (diagIdx in 0 until availableDiagonalsCount){
            counts.replaceAll { 0 }
            var y = max(0, halfDiagonalsCount - diagIdx)
            var x = max(0, diagIdx - halfDiagonalsCount)
            while(x < BOARD_SIZE && y < BOARD_SIZE){
                if(countNext(board[x][y], counts, userIdByIndex))
                    return false
                x++
                y++
            }
        }

        // diagonal right to left
        for (diagIdx in 0 until availableDiagonalsCount){
            counts.replaceAll { 0 }
            var y = max(0, halfDiagonalsCount - diagIdx)
            var x = min(BOARD_SIZE - 1, (BOARD_SIZE - 1) + halfDiagonalsCount - diagIdx)
            while(x >= 0 && y < BOARD_SIZE){
                if(countNext(board[x][y], counts, userIdByIndex))
                    return false
                x--
                y++
            }
        }

        return true
    }

    private fun countNext(field: UserInGame?, counts: MutableList<Int>, userIds: List<Int>): Boolean{
        if(field == null){
            counts.replaceAll { it+1 }
        }
        else{
            counts.forEachIndexed { i, count ->
                counts[i] = if (field.id == userIds[i]) count + 1
                else 0
            }
        }
        return counts.any { it >= WIN_LINE_LENGTH }
    }

    fun findWinLine(userInGame: UserInGame, x: Int, y:Int): Collection<Field>?{
        // vertical
        val result = mutableListOf<Field>()
        for(i in 0 until BOARD_SIZE){
            if(board[i][y] == userInGame)
                result.add(Field(i, y))
            else
                result.clear()
            if(result.size == WIN_LINE_LENGTH)
                return result
        }

        // horizontal
        result.clear()
        for(j in 0 until BOARD_SIZE){
            if(board[x][j] == userInGame)
                result.add(Field(x, j))
            else
                result.clear()
            if(result.size == WIN_LINE_LENGTH)
                return result
        }

        // diagonal left to right
        result.clear()
        var stepsToStartPosition = min(x, y)
        var i = x - stepsToStartPosition
        var j = y - stepsToStartPosition
        while(i < BOARD_SIZE && j < BOARD_SIZE){
            if(board[i][j] == userInGame)
                result.add(Field(i, j))
            else
                result.clear()
            if(result.size == WIN_LINE_LENGTH)
                return result
            i++;j++
        }

        // diagonal right to left
        result.clear()
        stepsToStartPosition = min(x, BOARD_SIZE - y - 1)
        i = x - stepsToStartPosition
        j = y + stepsToStartPosition
        while(i < BOARD_SIZE && j >= 0){
            if(board[i][j] == userInGame)
                result.add(Field(i, j))
            else
                result.clear()
            if(result.size == WIN_LINE_LENGTH)
                return result
            i++;j--
        }

        return null
    }
}
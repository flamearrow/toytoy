package band.mlgb.myapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import band.mlgb.myapplication.Matrix.INITIAL
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.random.Random

enum class Block {
    WALL, COW, GRASS, VACANT
}

object Matrix {
    private var cowX = 2
    private var cowY = 1

    private var random = Random(23)

    var currentMatrix: MutableList<MutableList<Block>> = mutableListOf(
        mutableListOf(Block.WALL, Block.WALL, Block.WALL, Block.WALL),
        mutableListOf(Block.VACANT, Block.VACANT, Block.COW, Block.VACANT),
        mutableListOf(Block.GRASS, Block.VACANT, Block.WALL, Block.VACANT),
        mutableListOf(Block.VACANT, Block.VACANT, Block.VACANT, Block.WALL),
    )

    var height = currentMatrix.size
    var width = currentMatrix[0].size

    var foundGrass = false

    fun nextMatrix(): List<List<Block>> {
        if (foundGrass) {
            return currentMatrix
        }
        Log.d("BGLM", "cowX: $cowX, cowY: $cowY")
        while (true) {
            when (random.nextInt(4)) {
                0 -> { // UP
                    if (cowY - 1 < 0) {
                        continue
                    }
                    if (currentMatrix[cowY - 1][cowX] == Block.WALL) { // can't go up
                        continue
                    }

                    if (currentMatrix[cowY - 1][cowX] == Block.VACANT) { // go up
                        currentMatrix[cowY][cowX] = Block.VACANT
                        currentMatrix[cowY - 1][cowX] = Block.COW
                        cowY -= 1
                        break
                    }

                    if (currentMatrix[cowY - 1][cowX] == Block.GRASS) {
                        foundGrass = true
                        currentMatrix[cowY][cowX] = Block.VACANT
                        currentMatrix[cowY - 1][cowX] = Block.GRASS
                        cowY -= 1
                        break
                    }
                }

                1 -> { // DOWN
                    if (cowY + 1 >= height) {
                        continue
                    }
                    if (currentMatrix[cowY + 1][cowX] == Block.WALL) { // can't go down
                        continue
                    }

                    if (currentMatrix[cowY + 1][cowX] == Block.VACANT) { // go down
                        currentMatrix[cowY][cowX] = Block.VACANT
                        currentMatrix[cowY + 1][cowX] = Block.COW
                        cowY += 1
                        break
                    }

                    if (currentMatrix[cowY + 1][cowX] == Block.GRASS) {
                        foundGrass = true
                        currentMatrix[cowY][cowX] = Block.VACANT
                        currentMatrix[cowY + 1][cowX] = Block.GRASS
                        cowY += 1
                        break
                    }
                }

                2 -> { // LEFT
                    if (cowX - 1 < 0) {
                        continue
                    }
                    if (currentMatrix[cowY][cowX - 1] == Block.WALL) { // can't go left
                        continue
                    }

                    if (currentMatrix[cowY][cowX - 1] == Block.VACANT) { // go left
                        currentMatrix[cowY][cowX] = Block.VACANT
                        currentMatrix[cowY][cowX - 1] = Block.COW
                        cowX -= 1
                        break
                    }

                    if (currentMatrix[cowY][cowX - 1] == Block.GRASS) {
                        foundGrass = true
                        currentMatrix[cowY][cowX] = Block.VACANT
                        currentMatrix[cowY][cowX - 1] = Block.GRASS
                        cowX -= 1
                        break
                    }

                }

                3 -> { // RIGHT
                    if (cowX + 1 >= width) {
                        continue
                    }
                    if (currentMatrix[cowY][cowX + 1] == Block.WALL) { // can't go right
                        continue
                    }

                    if (currentMatrix[cowY][cowX + 1] == Block.VACANT) { // go right
                        currentMatrix[cowY][cowX] = Block.VACANT
                        currentMatrix[cowY][cowX + 1] = Block.COW
                        cowX += 1
                        break
                    }

                    if (currentMatrix[cowY][cowX + 1] == Block.GRASS) {
                        foundGrass = true
                        currentMatrix[cowY][cowX] = Block.VACANT
                        currentMatrix[cowY][cowX + 1] = Block.GRASS
                        cowX += 1
                        break
                    }
                }
            }
        }
        Log.d("BGLM", "cowX: $cowX, cowY: $cowY")
        Log.d("BGLM", "found: $foundGrass, cowY: $cowY")

        val ret = mutableListOf<List<Block>>()
        currentMatrix.forEach {
            val newList = mutableListOf<Block>()
            it.forEach {
                newList.add(it)
            }
            ret.add(newList)
        }


        return ret
    }

    val INITIAL = mutableListOf(
        mutableListOf(Block.WALL, Block.WALL, Block.WALL, Block.WALL),
        mutableListOf(Block.VACANT, Block.VACANT, Block.COW, Block.VACANT),
        mutableListOf(Block.GRASS, Block.VACANT, Block.WALL, Block.VACANT),
        mutableListOf(Block.VACANT, Block.VACANT, Block.VACANT, Block.WALL),
    )
}

class MatrixVM : ViewModel() {
    var shouldUpdate: MutableStateFlow<Boolean> = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    var updateFlow: Flow<Unit> = shouldUpdate.flatMapLatest { shouldUpdate ->
        if (shouldUpdate) {
            flow {
                while (true) {
                    delay(1000)
                    emit(Unit)
                }
            }
        } else {
            flowOf()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    var matrixFlow: StateFlow<List<List<Block>>> = updateFlow.mapLatest {
        Matrix.nextMatrix()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = INITIAL
    )


    fun startUpdate() {
        shouldUpdate.update {
            true
        }
    }

    fun stopUpdate() {
        shouldUpdate.update {
            false
        }
    }
}
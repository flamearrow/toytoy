package band.mlgb.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import band.mlgb.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val vm = viewModel<MatrixVM>()
    val currentMatrix by vm.matrixFlow.collectAsState()
    Log.d("BGLM", "compose $currentMatrix")
    MatrixScreen(currentMatrix, onStart = { vm.startUpdate() }, onStop = { vm.stopUpdate() })
}


@Composable
fun MatrixScreen(matrix: List<List<Block>>, onStart: () -> Unit, onStop: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        matrix.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { block ->
                    when (block) {
                        Block.WALL -> {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.Black)
                            )
                        }

                        Block.COW -> {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.Red)
                            )
                        }

                        Block.GRASS -> {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.Green)
                            )
                        }

                        Block.VACANT -> {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.White)
                            )
                        }
                    }
                }
            }
        }

        Row {
            Button(onClick = onStart) {
                Text(text = "Start moving")
            }

            Button(onClick = onStop) {
                Text(text = "Stop moving")
            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        MatrixScreen(matrix = Matrix.currentMatrix, onStart = {}, onStop = {})
    }
}
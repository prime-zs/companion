package com.prime.sample

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.prime.sample.ui.theme.CompanionTheme
import com.primex.extra.*
import com.primex.preferences.Preferences
import com.primex.preferences.stringPreferenceKey
import com.primex.toolkit.Toolkit
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompanionTheme {

                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxSize()
                ) {

                    val (s, d) = remember {
                        mutableResultOf(0){result ->
                            flow {
                                var x = 0
                                while (true){
                                    kotlinx.coroutines.delay(500)
                                    emit(x++)
                                }
                            }.onEach {
                                if (it % 50 ==0)
                                    result.emit(Result.State.Error("even"))
                                else
                                    result.emit(it)
                            }
                                .launchIn(lifecycleScope)
                        }
                    }

                    Text(text = "$s")
                    Text(text = "$d")
                }
            }
        }
    }
}


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun Test1() {
    // A surface container using the 'background' color from the theme
    Surface(color = MaterialTheme.colors.background) {
        Greeting("Android")

        val context = LocalContext.current

        val preferences = Preferences.get(context)


        Frame {
            Label(text = "jjkjkj")
        }


        val x = with(preferences) { get(stringPreferenceKey("hjh")).observeAsState() }

        GlobalScope.launch {
            val amplitude = Toolkit
            val list = context.contentResolver.Audios

            val start = System.currentTimeMillis()
            val (res, du) = amplitude.amplitudes(list.last())
            Log.i(TAG, "onCreate: $res")
            val elapsed = System.currentTimeMillis() - start
            Log.i(TAG, "onCreate: $elapsed")
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CompanionTheme {
        Greeting("Android")
    }
}
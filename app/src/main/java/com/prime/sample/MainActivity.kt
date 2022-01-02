package com.prime.sample

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.prime.sample.ui.theme.CompanionTheme
import com.primex.extra.*
import com.primex.preferences.Preferences
import com.primex.preferences.stringPreferenceKey
import com.primex.toolkit.Toolkit
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.lang.IllegalStateException

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

                    var query by remember {
                        mutableStateOf("")
                    }

                    val res = remember {
                        buildResultOfList(emptyList(), lifecycleScope, flow {
                            val range = (0..500)

                            var int = 0

                            while (true) {
                                kotlinx.coroutines.delay(500)
                                if (int > 50)
                                    throw IllegalStateException()
                                if (int == 30)
                                    emit(emptyList())
                                else
                                    emit(listOf(int))
                                int++
                            }
                        })
                    }

                    val (s, d) = res

                    Column {
                        Text(text = s.toString())
                        Text(text = "$d")
                    }
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
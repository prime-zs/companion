package com.prime.sample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.prime.sample.ui.theme.CompanionTheme
import com.primex.preferences.Preferences
import com.primex.preferences.stringPreferenceKey
import com.primex.toolkit.Toolkit
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompanionTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")

                    val preferences = Preferences.get(this)

                val x =    with(preferences){get(stringPreferenceKey("hjh")).observeAsState()}

                    GlobalScope.launch {
                        val amplitude = Toolkit
                        val list = contentResolver.Audios

                        val start = System.currentTimeMillis()
                        val (res, du) = amplitude.amplitudes(list.last())
                        Log.i(TAG, "onCreate: $res")
                        val elapsed = System.currentTimeMillis() - start
                        Log.i(TAG, "onCreate: $elapsed")
                    }
                }
            }
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
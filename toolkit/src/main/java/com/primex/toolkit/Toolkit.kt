package com.primex.toolkit

import android.util.Log
import com.primex.toolkit.models.Amplitudes
import com.primex.toolkit.models.Amplitudes2
import com.primex.toolkit.models.check
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext as using


private const val TAG = "Toolkit"
object Toolkit {


    /**
     * Computes Amplitudes on [Dispatchers.IO] thread.
     * @throws Amplitudes.Exception
     */
    suspend fun amplitudes(path: String): Amplitudes = using(Dispatchers.Default) {
        val jResult = amplitudesFromJNI(path, 1, 1)
        // check for errors and throw exception if any.
        jResult.check()
        val values = jResult.amplitudes.split("\n")
        val array = IntArray(values.size)
        values.forEachIndexed { index, value ->
            if (value.isNotEmpty()) {
                array[index] = value.toInt()
            } else
                Log.i(TAG, " empty index: $index: $value")
        }
        Amplitudes(array, duration = jResult.duration.toLong())
    }



    private external fun amplitudesFromJNI(
        path: String,
        compressionType: Int,
        fps: Int,
    ): Amplitudes2




    init {
        System.loadLibrary("toolkit_cpp")
    }
}
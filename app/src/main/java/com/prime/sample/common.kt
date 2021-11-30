package com.prime.sample


import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore
import androidx.annotation.WorkerThread


private fun Cursor.indexes(projection: Array<String>): Array<Int> =
    Array(projection.size) { index ->
        getColumnIndex(projection[index])
    }


/**
 * Returns all the audios recognised by [MediaStore] as [List] [MediaItem]
 */
val ContentResolver.Audios: List<String>
    @WorkerThread
    get() {
        val list = ArrayList<String>()


        val contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        // define params
        val projection = arrayOf(
            MediaStore.Audio.AudioColumns._ID, //0
            MediaStore.Audio.AudioColumns.TITLE, // 1
            MediaStore.Audio.AudioColumns.ARTIST, // 2
            MediaStore.Audio.AudioColumns.ALBUM, // 3
            MediaStore.Audio.AudioColumns.ALBUM_ID, // 4
            MediaStore.Audio.AudioColumns.DATE_ADDED,  //5
            MediaStore.Audio.AudioColumns.COMPOSER, // 6
            MediaStore.Audio.AudioColumns.YEAR, // 7
            MediaStore.Audio.AudioColumns.DATA, // 8
            MediaStore.Audio.AudioColumns.DURATION, // 9
            MediaStore.Audio.AudioColumns.MIME_TYPE, // 10
        )

        query(contentUri, projection, selection, null, null)?.use { cursor ->

            val indexes = cursor.indexes(projection)

            // loop to create list.
            while (cursor.moveToNext()) {

                val id = cursor.getLong(indexes[0])
                val title = cursor.getString(indexes[1])
                val artist = cursor.getString(indexes[2])
                val album = cursor.getString(indexes[3])
                val albumID = cursor.getLong(indexes[4])
                val dateAdded = cursor.getLong(indexes[5])
                val composer = cursor.getString(indexes[6])
                val year = cursor.getInt(indexes[7])
                val data = cursor.getString(indexes[8])
                val duration = cursor.getInt(indexes[9])
                val mimeType = cursor.getString(indexes[10])


                // construct and add item to list
                list += data
            }
        }
        return list
    }

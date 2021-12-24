package com.prime.sample


import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.primex.extra.*


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

/**
 * A representation of ListTile for [LazyColumn]s etc.
 *
 *  The [ListTile] constitutes of [secondaryText], [text], [trailing], [icon] and [overlineText]. It has
 *  width of fillWidth and height of [WrapContent].
 *
 *  You can also change colors using [enabled] and [selected]. The [selected] adds a background of
 *  [LocalContentColor] with alpha = [ContentAlpha.Indication].
 *  @param modifier can be used to add horizontal padding, clickable etc.
 *
 */
@Composable
fun ListTile(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    secondaryText: @Composable (() -> Unit)? = null,
    overlineText: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit),
    icon: @Composable (() -> Unit)?,
) {
    val typography = MaterialTheme.typography

    val styledText = applyTextStyle(
        typography.subtitle1,
        if (enabled) ContentAlpha.high else ContentAlpha.disabled,
        text
    )!!

    val styledSecondaryText = applyTextStyle(
        typography.body2,
        if (enabled) ContentAlpha.medium else ContentAlpha.disabled,
        secondaryText
    )

    val styledOverlineText = applyTextStyle(
        typography.overline,
        if (enabled) ContentAlpha.high else ContentAlpha.disabled,
        overlineText
    )

    val styledTrailing = applyTextStyle(
        typography.caption,
        if (enabled) ContentAlpha.high else ContentAlpha.disabled,
        trailing
    )

    val bg by animateColorAsState(
        targetValue = if (selected) LocalContentColor.current.copy(
            ContentAlpha.Indication
        ) else Color.Transparent
    )

    Row(
        modifier = Modifier
            .background(color = bg)
            .then(
                modifier
                    .padding(vertical = Dp.PaddingNormal)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.invoke()
        Column(
            modifier = Modifier
                .padding(horizontal = Dp.PaddingNormal)
                .weight(1f), verticalArrangement = Arrangement.Center
        ) {
            styledOverlineText?.invoke()
            styledText.invoke()
            styledSecondaryText?.invoke()
        }
        styledTrailing?.invoke()
    }
}

private fun applyTextStyle(
    textStyle: TextStyle,
    contentAlpha: Float,
    icon: @Composable (() -> Unit)?
): @Composable (() -> Unit)? {
    if (icon == null) return null
    return {
        CompositionLocalProvider(LocalContentAlpha provides contentAlpha) {
            ProvideTextStyle(textStyle, icon)
        }
    }
}


@Composable
fun Genre(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    name: String,
    subtitle: String,
) {
    val shape = RoundedCornerShape(4.dp)

    GridTile(
        modifier = Modifier
            .clip(shape)
            .width(width = 100.dp)
            .wrapContentHeight()
            .then(modifier),
        selected = selected,
        enabled = false,
        padding = PaddingValues(Dp.PaddingNormal),
        text = { Label(text = name, maxLines = 2, textAlign = TextAlign.Center) },
        secondaryText = { Label(text = subtitle, textAlign = TextAlign.Center) }
    ) {

        Frame(
            color = Color.Transparent,
            shape = CircleShape,
            modifier = Modifier
                .padding(top = Dp.PaddingNormal)
                .size(60.dp),
            border = BorderStroke(3.dp, LocalContentColor.current.copy(LocalContentAlpha.current)),
        ) {
            val char = name[0].uppercaseChar()
            Header(
                text = "$char",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.wrapContentSize(),
                style = MaterialTheme.typography.h4
            )
        }
    }
}

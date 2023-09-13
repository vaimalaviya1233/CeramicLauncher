package one.zagura.CeramicLauncher.data

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.palette.graphics.Palette

class MediaPlayerData(
    val name: String,
    val album: String?,
    val artist: String?,

    val cover: Drawable?,
    val palette: Palette?,

    val onTap: ((View) -> Unit)?,
    val previous: (View) -> Unit,
    val next: (View) -> Unit,
    val togglePause: (ImageView) -> Unit,
    val isPlaying: () -> Boolean,
)
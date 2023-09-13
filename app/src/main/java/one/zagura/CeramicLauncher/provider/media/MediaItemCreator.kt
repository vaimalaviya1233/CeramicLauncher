package one.zagura.CeramicLauncher.provider.media

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.PlaybackState
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.data.MediaPlayerData

object MediaItemCreator {

    fun create(context: Context, controller: MediaController, mediaMetadata: MediaMetadata): MediaPlayerData {

        val title = mediaMetadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
            ?: mediaMetadata.getString(MediaMetadata.METADATA_KEY_TITLE)
            ?: null
        val album = mediaMetadata.getString(MediaMetadata.METADATA_KEY_ALBUM)
            ?: mediaMetadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE)
            ?: null
        val artist = mediaMetadata.getString(MediaMetadata.METADATA_KEY_ARTIST)
            ?: mediaMetadata.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)
            ?: mediaMetadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_DESCRIPTION)
            ?: null

        val coverBmp = mediaMetadata.getBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON)
            ?: mediaMetadata.getBitmap(MediaMetadata.METADATA_KEY_ART)
            ?: mediaMetadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
            ?: null

        val cover = coverBmp?.let(::BitmapDrawable) ?: context.getDrawable(R.drawable.ic_play)!!

        return MediaPlayerData(
            name = title.toString(),
            album = album,
            artist = artist,
            palette = coverBmp?.let { Palette.from(it).generate() },
            onTap = {
                controller.sessionActivity?.send()
            },
            previous = {
                controller.transportControls.skipToPrevious()
            },
            next = {
                controller.transportControls.skipToNext()
            },
            togglePause = {
                if (controller.playbackState?.state == PlaybackState.STATE_PLAYING) {
                    controller.transportControls.pause()
                    it.setImageResource(R.drawable.ic_play)
                } else {
                    controller.transportControls.play()
                    it.setImageResource(R.drawable.ic_pause)
                }
            },
            isPlaying = {
                controller.playbackState?.state == PlaybackState.STATE_PLAYING
            },
            cover = cover,
        )
    }
}
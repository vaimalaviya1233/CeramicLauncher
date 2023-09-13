package one.zagura.CeramicLauncher.ui.view.feed

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.media.AudioManager
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.graphics.luminance
import androidx.core.view.isVisible
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.toFloatPixels
import io.posidon.android.conveniencelib.units.toPixels
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.data.MediaPlayerData
import one.zagura.CeramicLauncher.provider.notifications.NotificationService
import one.zagura.CeramicLauncher.ui.view.BackdropImageView
import one.zagura.CeramicLauncher.util.storage.Settings
import one.zagura.CeramicLauncher.util.theme.ColorTools

class MusicCard(c: Context) : CardView(c), FeedSection {

    val musicService = context.getSystemService(AudioManager::class.java)

    val musicCardImage = BackdropImageView(context).apply {
        scaleType = ImageView.ScaleType.FIT_END
    }

    val musicCardTrackTitle = TextView(context).apply {
        textSize = 18f
    }

    val musicCardTrackSubtitle = TextView(context).apply {
        textSize = 15f
    }
    
    val musicPrev = ImageView(context).apply {
        setImageResource(R.drawable.ic_track_prev)
        setOnClickListener {
            musicService.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS))
            musicService.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PREVIOUS))
            musicPlay.setImageResource(R.drawable.ic_pause)
        }
    }

    val musicPlay = ImageView(context).apply {
        setImageResource(R.drawable.ic_play)
        setOnClickListener {
            it as ImageView
            if (musicService.isMusicActive) {
                musicService.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE))
                musicService.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE))
                it.setImageResource(R.drawable.ic_play)
            } else {
                musicService.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY))
                musicService.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY))
                it.setImageResource(R.drawable.ic_pause)
            }
        }
    }

    val musicNext = ImageView(context).apply {
        setImageResource(R.drawable.ic_track_next)
        setOnClickListener {
            musicService.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT))
            musicService.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT))
            musicPlay.setImageResource(R.drawable.ic_pause)
        }
    }

    val musicCardOverImg = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        val p = context.resources.getDimension(R.dimen.notification_primary_area_padding).toInt()
        setPaddingRelative(p, p, p, p)

        val linearLayout = LinearLayout(context).apply {
            this.orientation = LinearLayout.HORIZONTAL
            this.gravity = Gravity.BOTTOM
            setPadding(0, 8.dp.toPixels(context), 0, 0)
            layoutDirection = LAYOUT_DIRECTION_LTR

            addView(musicPrev, LinearLayout.LayoutParams(36.dp.toPixels(context), 32.dp.toPixels(context)))
            addView(musicPlay, LinearLayout.LayoutParams(36.dp.toPixels(context), 32.dp.toPixels(context)).apply {
                setMargins(12.dp.toPixels(context), 0, 12.dp.toPixels(context), 0)
            })
            addView(musicNext, LinearLayout.LayoutParams(36.dp.toPixels(context), 32.dp.toPixels(context)))
        }

        addView(musicCardTrackTitle, LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        addView(musicCardTrackSubtitle, LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        addView(linearLayout, LayoutParams(MATCH_PARENT, WRAP_CONTENT))
    }

    init {
        radius = 16.dp.toFloatPixels(context)
        cardElevation = 0f
        setCardBackgroundColor(context.resources.getColor(R.color.ui_card_background))

        addView(musicCardImage, LayoutParams(MATCH_PARENT, MATCH_PARENT))
        addView(musicCardOverImg, LayoutParams(MATCH_PARENT, WRAP_CONTENT))

        isVisible = false
        NotificationService.onMediaUpdate = ::updateTrack
        NotificationService.instance?.updateMediaItem(context)
    }

    override fun onDelete(feed: Feed) {
        NotificationService.onMediaUpdate = {}
    }

    override fun updateTheme(activity: Activity) {
        val marginX = Settings["notif:margin_x", 0].dp.toPixels(context)
        val marginY = Settings["feed:card_margin_y", 9].dp.toPixels(context)
        (layoutParams as MarginLayoutParams).apply {
            leftMargin = marginX
            rightMargin = marginX
            bottomMargin = marginY
            topMargin = marginY
        }
        radius = Settings["notif:radius", 0].dp.toFloatPixels(context)
    }

    @SuppressLint("SetTextI18n")
    fun updateTrack(data: MediaPlayerData?) {
        if (data == null) {
            isVisible = false
            return
        }
        isVisible = true
        setOnClickListener(data.onTap)
        val bg = Settings["notif:background_color", -0x1]
        val (bgColor, fgColor) = if (data.palette == null) {
            bg to Settings["notif:background_color", -0x1]
        } else {
            val bgColor = data.palette.getDominantColor(bg)
            val fgColor = if (bgColor.luminance > 0.6f)
                data.palette.getDarkVibrantColor(0xff000000.toInt())
            else data.palette.getLightMutedColor(0xffffffff.toInt())
            bgColor to ColorTools.makeContrasty(fgColor, bgColor)
        }
        setCardBackgroundColor(bgColor)
        musicCardImage.setImageDrawable(
            LayerDrawable(arrayOf(
                data.cover,
                GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(bgColor, bgColor and 0x00ffffff))
            )))
        with(musicCardTrackTitle) {
            setTextColor(fgColor)
            text = data.name
        }
        with(musicCardTrackSubtitle) {
            setTextColor(fgColor)
            text = "${data.album} • ${data.artist}"
        }
        musicPrev.imageTintList = ColorStateList.valueOf(fgColor)
        musicPlay.imageTintList = ColorStateList.valueOf(fgColor)
        musicNext.imageTintList = ColorStateList.valueOf(fgColor)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            if (musicService.isMusicActive)
                musicPlay.setImageResource(R.drawable.ic_pause)
            else
                musicPlay.setImageResource(R.drawable.ic_play)
        }
    }

    override fun toString() = "music"
}
package one.zagura.CeramicLauncher.ui.view.setting

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Typeface
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ArrayRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.setMargins
import androidx.core.widget.NestedScrollView
import io.posidon.android.conveniencelib.Device
import io.posidon.android.conveniencelib.getStatusBarHeight
import io.posidon.android.conveniencelib.units.dp
import io.posidon.android.conveniencelib.units.toPixels
import one.zagura.CeramicLauncher.R
import one.zagura.CeramicLauncher.util.storage.Settings
import one.zagura.CeramicLauncher.util.Gestures
import one.zagura.CeramicLauncher.util.Tools

@JvmInline
value class SettingsViewScope(val viewGroup: ViewGroup)

internal inline fun Activity.configureWindowForSettings() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) window.setDecorFitsSystemWindows(false)
    else window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
}

inline fun Activity.setSettingsContentView(@StringRes titleId: Int, builder: SettingsViewScope.() -> Unit) {
    setContentView(NestedScrollView(this).apply {
        setPadding(0, getStatusBarHeight(), 0, Tools.navbarHeight)
        isVerticalFadingEdgeEnabled = true
        setFadingEdgeLength(64.dp.toPixels(context))
        addView(LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            val headerHeight = (Device.screenHeight(context) / 3)
                .coerceAtLeast(128.dp.toPixels(context))
                .coerceAtMost(256.dp.toPixels(context))
            addView(TextView(context).apply {
                setText(titleId)
                gravity = Gravity.CENTER
                setTextColor(0xffffffff.toInt())
                textSize = 32f
                typeface = Typeface.SERIF
            }, ViewGroup.LayoutParams(MATCH_PARENT, headerHeight))
            builder(SettingsViewScope(this))
        }, ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT))
    })
}

inline fun SettingsViewScope.card(builder: SettingsViewScope.() -> Unit) {
    viewGroup.addView(LinearLayout(viewGroup.context).apply {
        orientation = LinearLayout.VERTICAL
        background = context.getDrawable(R.drawable.ui_card)
        val vp = 12.dp.toPixels(context)
        setPadding(0, vp, 0, vp)
        clipToPadding = false
        builder(SettingsViewScope(this))
    }, ViewGroup.MarginLayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
        setMargins(8.dp.toPixels(viewGroup))
    })
}

fun SettingsViewScope.numberSeekBar(
    @StringRes
    labelId: Int,
    key: String,
    default: Int,
    max: Int,
    startsWith1: Boolean = false,
) = NumberBarSettingView(viewGroup.context, key, default, labelId, startsWith1, max)
    .also { viewGroup.addView(it, settingsEntryLayoutParams()) }

fun SettingsViewScope.switch(
    @StringRes
    labelId: Int,
    @DrawableRes
    iconId: Int,
    key: String,
    default: Boolean,
) = SwitchSettingView(viewGroup.context,
    key,
    default,
    labelId,
    iconId
).also { viewGroup.addView(it, settingsEntryLayoutParams()) }

fun SettingsViewScope.color(
    @StringRes
    labelId: Int,
    @DrawableRes
    iconId: Int = R.drawable.ic_color,
    key: String,
    @ColorInt
    default: Int,
    hasAlpha: Boolean = true,
) = ColorSettingView(viewGroup.context, key, default, labelId, iconId, hasAlpha)
    .also { viewGroup.addView(it, settingsEntryLayoutParams()) }


fun SettingsViewScope.spinner(
    @StringRes
    labelId: Int,
    @DrawableRes
    iconId: Int,
    key: String,
    default: Int,
    @ArrayRes
    array: Int,
) = SpinnerSettingView(viewGroup.context, key, default, labelId, iconId).apply {
    this.data = resources.getStringArray(array)
    selectionI = Settings[key, default]
}.also { viewGroup.addView(it, settingsEntryLayoutParams()) }

fun SettingsViewScope.actionSelector(
    @StringRes
    labelId: Int,
    @DrawableRes
    iconId: Int,
    key: String,
    default: String,
) = ActionSelectorSettingView(viewGroup.context, key, Gestures.getIndex(default), labelId, iconId)
    .also { viewGroup.addView(it, settingsEntryLayoutParams()) }

@SuppressLint("UseCompatLoadingForDrawables")
fun SettingsViewScope.clickable(
    @StringRes
    labelId: Int,
    @DrawableRes
    iconId: Int,
    onClick: (View) -> Unit,
) = ClickableSettingView(
    viewGroup.context,
    viewGroup.context.getString(labelId),
    viewGroup.context.getDrawable(iconId)!!,
).apply { setOnClickListener(onClick) }
    .also { viewGroup.addView(it, settingsEntryLayoutParams()) }

@SuppressLint("UseCompatLoadingForDrawables")
inline fun SettingsViewScope.custom(
    viewBuilder: () -> View
) = viewBuilder().also { viewGroup.addView(it, settingsEntryLayoutParams()) }

fun SettingsViewScope.switchTitle(
    @StringRes
    labelId: Int,
    key: String,
    default: Boolean,
) = HeaderSwitchSettingView(viewGroup.context).apply {
    this.label = context.getString(labelId)
    this.key = key
    this.value = Settings[key, default]
}.also { viewGroup.addView(it, settingsTitleLayoutParams()) }

fun SettingsViewScope.title(
    @StringRes
    labelId: Int,
) = HeaderSettingView(viewGroup.context).apply {
    this.label = context.getString(labelId)
}.also { viewGroup.addView(it, settingsTitleLayoutParams()) }

fun SettingsViewScope.separator() = View(viewGroup.context).apply {
    setBackgroundResource(R.drawable.ui_separator)
}.also { viewGroup.addView(it, MarginLayoutParams(MATCH_PARENT, 1.dp.toPixels(viewGroup.context)).apply {
    topMargin = 16.dp.toPixels(viewGroup.context)
    bottomMargin = topMargin
}) }

fun SettingsViewScope.settingsEntryLayoutParams() = ViewGroup.MarginLayoutParams(
    MATCH_PARENT, WRAP_CONTENT
).apply {
    leftMargin = 12.dp.toPixels(viewGroup)
    rightMargin = 12.dp.toPixels(viewGroup)
}

fun SettingsViewScope.settingsTitleLayoutParams() = ViewGroup.MarginLayoutParams(
    MATCH_PARENT, WRAP_CONTENT
).apply {
    topMargin = 12.dp.toPixels(viewGroup)
}
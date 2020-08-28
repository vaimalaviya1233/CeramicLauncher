package posidon.launcher.desktop

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.GridView
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import posidon.launcher.Main
import posidon.launcher.R
import posidon.launcher.items.users.DrawerAdapter
import posidon.launcher.search.SearchActivity
import posidon.launcher.storage.Settings
import posidon.launcher.tools.Tools

class AppList : FragmentActivity() {

    private lateinit var gridView: GridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Settings.init(applicationContext)
        setContentView(R.layout.desktop_app_list)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        gridView = findViewById(R.id.apps)

        gridView.adapter = DrawerAdapter()
        gridView.setOnItemClickListener {
            _, view, i, _ -> Main.apps[i].open(this@AppList, view)
            finish()
        }

        findViewById<ImageView>(R.id.blur).setImageBitmap(Tools.blurredWall(Settings["drawer:blur:rad", 15f]))
    }

    fun openSearch(v: View) { startActivity(Intent(this, SearchActivity::class.java), ActivityOptions.makeCustomAnimation(this, R.anim.fadein, R.anim.fadeout).toBundle()) }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ESCAPE) finish()
        return super.onKeyDown(keyCode, event)
    }
}

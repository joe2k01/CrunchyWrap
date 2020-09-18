package io.github.joe2k01.crunchywrap

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val pagesAdapter = PagesAdapter(supportFragmentManager)
        viewpager.adapter = pagesAdapter
        tab_layout.setupWithViewPager(viewpager)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tab_layout.getTabAt(0)?.icon =
                ContextCompat.getDrawable(baseContext, R.drawable.ic_thumb_up)
            tab_layout.getTabAt(1)?.icon = ContextCompat.getDrawable(baseContext, R.drawable.ic_new)
        } else {
            tab_layout.getTabAt(0)?.icon =
                ResourcesCompat.getDrawable(baseContext.resources, R.drawable.ic_thumb_up, null)
            tab_layout.getTabAt(1)?.icon =
                ResourcesCompat.getDrawable(baseContext.resources, R.drawable.ic_new, null)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }

        return true
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (Intent.ACTION_SEARCH == intent?.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            invalidateOptionsMenu()

            val mIntent = Intent(this, SearchResultActivity::class.java)
            mIntent.putExtra("query", query)
            startActivity(mIntent)
        }
    }
}

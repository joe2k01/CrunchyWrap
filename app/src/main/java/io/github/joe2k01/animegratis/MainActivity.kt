package io.github.joe2k01.animegratis

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ApiCalls(this).authenticate()

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val pagesAdapter = PagesAdapter(supportFragmentManager)
        viewpager.adapter = pagesAdapter
        tab_layout.setupWithViewPager(viewpager)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tab_layout.getTabAt(0)?.icon = getDrawable(R.drawable.ic_thumb_up)
            tab_layout.getTabAt(1)?.icon = getDrawable(R.drawable.ic_new)
        } else {
            tab_layout.getTabAt(0)?.icon = resources.getDrawable(R.drawable.ic_thumb_up)
            tab_layout.getTabAt(1)?.icon = resources.getDrawable(R.drawable.ic_new)
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

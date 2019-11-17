package io.github.joe2k01.crunchywrap

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_search_result.*
import kotlinx.android.synthetic.main.fragment_newest.recyclerView
import org.json.JSONObject

class SearchResultActivity : AppCompatActivity() {
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            if (intent != null) {
                val results = intent.getStringArrayListExtra("results")!!
                val size = results.size
                val titles = Array(size) { "" }
                val portraitImages = Array(size) { "" }
                val landscapeImages = Array(size) { "" }
                val descriptions = Array(size) { "" }
                val seriesIds = Array(size) { "" }
                for (x in results.indices) {
                    val json = JSONObject(results[x])
                    titles[x] = json.getString("name")
                    seriesIds[x] = json.getString("series_id")
                    descriptions[x] = json.getString("description")
                    val portrait = JSONObject(json.getString("portrait_image"))
                    val landscape = JSONObject(json.getString("landscape_image"))
                    portraitImages[x] = portrait.getString("full_url")
                    landscapeImages[x] = landscape.getString("full_url")
                }

                val linearLayoutManager = LinearLayoutManager(baseContext)
                val animeAdapter = AnimeAdapter(
                    baseContext, titles, portraitImages, landscapeImages,
                    descriptions, seriesIds
                )

                recyclerView.apply {
                    setHasFixedSize(true)
                    layoutManager = linearLayoutManager
                    adapter = animeAdapter
                }
            }

            loading_s.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        LocalBroadcastManager.getInstance(baseContext)
            .registerReceiver(receiver, IntentFilter(ApiCalls(baseContext).searchIntent))

        supportActionBar?.title = '"' + intent.getStringExtra("query")!! + '"'

        val apiCalls = ApiCalls(baseContext)
        apiCalls.search(intent.getStringExtra("query")!!)
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(baseContext).unregisterReceiver(receiver)

        super.onDestroy()
    }
}

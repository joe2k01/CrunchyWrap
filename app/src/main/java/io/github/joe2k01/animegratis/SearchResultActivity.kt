package io.github.joe2k01.animegratis

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_newest.*
import org.json.JSONObject

class SearchResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        val apiCalls = ApiCalls()
        val results = apiCalls.search(intent.getStringExtra("query")!!)

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
}

package io.github.joe2k01.animegratis

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_anime.*
import org.json.JSONObject

class AnimeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime)

        supportActionBar?.title = intent.getStringExtra("title")
        description.text = intent.getStringExtra("description")

        image.layoutParams.height = thumbnailHeight()

        Picasso.get()
            .load(intent.getStringExtra("image"))
            .fit()
            .into(image)

        description_layout.setOnClickListener {
            val rotation: Float
            val maxLines: Int
            if (more.rotation == 180F) {
                rotation = 0F
                maxLines = 0
                description.visibility = View.GONE
            } else {
                rotation = 180F
                maxLines = Int.MAX_VALUE
                description.visibility = View.VISIBLE
            }
            more.animate().rotation(rotation)
            description.maxLines = maxLines
        }

        val episodes = ApiCalls().getEpisodes(intent.getStringExtra("id")!!)
        val size = episodes.size
        val titles = Array(size) { "" }
        val numbers = Array(size) { "" }
        val mediaIds = Array(size) { "" }
        val screenShots = Array(size) { "" }
        for (x in 0 until (size - 1)) {
            val json = JSONObject(episodes[x])
            titles[x] = json.getString("name")
            numbers[x] = json.getString("episode_number")
            mediaIds[x] = json.getString("media_id")
            val screenshotImage = JSONObject(json.getString("screenshot_image"))
            screenShots[x] = screenshotImage.getString("large_url")
        }

        val linearLayoutManager = LinearLayoutManager(baseContext)
        val episodesAdapter = EpisodesAdapter(
            baseContext, titles, numbers, screenShots,
            mediaIds
        )

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            adapter = episodesAdapter
        }
    }

    private fun thumbnailHeight(): Int {
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels

        return 360 * width / 640
    }
}

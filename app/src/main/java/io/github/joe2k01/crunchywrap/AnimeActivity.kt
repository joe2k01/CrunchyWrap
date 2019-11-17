package io.github.joe2k01.crunchywrap

import android.content.*
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_anime.*
import org.json.JSONArray
import org.json.JSONObject


class AnimeActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var localeIds: ArrayList<String>
    private lateinit var sharedPreferences: SharedPreferences
    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (position != 0)
            sharedPreferences.edit().putString("locale", localeIds[position - 1]).apply()
        else
            sharedPreferences.edit().putString("locale", "").apply()
    }


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            if (intent != null) {
                val episodes = intent.getStringArrayListExtra("episodes")!!
                val size = episodes.size
                val titles = Array(size) { "" }
                val numbers = Array(size) { "" }
                val mediaIds = Array(size) { "" }
                val screenShots = Array(size) { "" }
                for (x in episodes.indices) {
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

            loading_a.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("locale", "").apply()

        LocalBroadcastManager.getInstance(baseContext)
            .registerReceiver(receiver, IntentFilter(ApiCalls(baseContext).EPISODES_INTENT))

        supportActionBar?.title = intent.getStringExtra("title")
        description.text = intent.getStringExtra("description")

        image.layoutParams.height = thumbnailHeight()

        themeActivity(intent.getStringExtra("image")!!, intent.getStringExtra("title")!!)

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

        val array = JSONArray(sharedPreferences.getString("locales", ""))
        localeIds = ArrayList()
        val locales = ArrayList<String>()

        locales.add(getString(R.string.default_locale))

        for (x in 0 until array.length()) {
            val locale = JSONObject(array.get(x).toString())
            localeIds.add(locale.get("locale_id").toString())
            locales.add(locale.get("label").toString())
        }
        val adapter = ArrayAdapter(this, R.layout.spinner_item, locales)
        locale_picker.adapter = adapter

        ApiCalls(baseContext).getEpisodes(intent.getStringExtra("id")!!)

        locale_picker.onItemSelectedListener = this
        locale_picker.background.colorFilter =
            PorterDuffColorFilter(resources.getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP)
    }

    private fun themeActivity(url: String, anime: String) {
        Thread {
            val bitmap: Bitmap = Picasso.get().load(url).get()
            Palette.from(bitmap).generate { palette ->
                var bar = 0
                var title = 0
                var status = 0
                if (palette?.lightVibrantSwatch != null) {
                    bar = palette.lightVibrantSwatch!!.rgb
                    title = palette.lightVibrantSwatch!!.titleTextColor
                }
                if (palette?.vibrantSwatch != null)
                    status = palette.vibrantSwatch!!.rgb

                if (bar == 0 && status != 0) {
                    bar = status
                    title = palette?.vibrantSwatch!!.titleTextColor
                }

                if (bar != 0) {
                    val arrow = ContextCompat.getDrawable(baseContext, R.drawable.ic_back)
                    val titleNoAlpha =
                        Color.rgb(Color.red(title), Color.green(title), Color.blue(title))
                    DrawableCompat.setTint(arrow!!, titleNoAlpha)

                    supportActionBar?.apply {
                        setBackgroundDrawable(ColorDrawable(bar))
                        setTitle(
                            Html.fromHtml(
                                "<font color='" + Integer.toHexString(titleNoAlpha).substring(
                                    2
                                ) + "'>" + anime + "</font>"
                            )
                        )
                        setHomeAsUpIndicator(arrow)
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        window.statusBarColor = status
                }
            }
        }.start()
    }

    private fun thumbnailHeight(): Int {
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels

        return 360 * width / 640
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(baseContext).unregisterReceiver(receiver)

        super.onDestroy()
    }
}

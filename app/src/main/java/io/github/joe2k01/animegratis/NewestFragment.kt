package io.github.joe2k01.animegratis


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_newest.*
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 */
class NewestFragment : Fragment() {
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            if (intent != null) {
                val newest = intent.getStringArrayExtra("newest")!!
                val size = newest.size
                val titles = Array(size) { "" }
                val portraitImages = Array(size) { "" }
                val landscapeImages = Array(size) { "" }
                val descriptions = Array(size) { "" }
                val seriesIds = Array(size) { "" }
                for (x in newest.indices) {
                    val json = JSONObject(newest[x]!!)
                    titles[x] = json.getString("name")
                    seriesIds[x] = json.getString("series_id")
                    descriptions[x] = json.getString("description")
                    val portrait = JSONObject(json.getString("portrait_image"))
                    val landscape = JSONObject(json.getString("landscape_image"))
                    portraitImages[x] = portrait.getString("full_url")
                    landscapeImages[x] = landscape.getString("full_url")
                }

                val linearLayoutManager = LinearLayoutManager(context)
                val animeAdapter = AnimeAdapter(
                    context!!, titles, portraitImages, landscapeImages,
                    descriptions, seriesIds
                )

                recyclerView.apply {
                    setHasFixedSize(true)
                    layoutManager = linearLayoutManager
                    adapter = animeAdapter
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LocalBroadcastManager.getInstance(context!!)
            .registerReceiver(receiver, IntentFilter(ApiCalls(context!!).NEWEST_INTENT))

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_newest, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiCalls = ApiCalls(context!!)
        apiCalls.getNewest()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(receiver)

        super.onDestroy()
    }
}

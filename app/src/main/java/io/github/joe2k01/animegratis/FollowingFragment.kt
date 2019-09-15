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
import kotlinx.android.synthetic.main.fragment_following.*
import kotlinx.android.synthetic.main.fragment_newest.recyclerView
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 */
class FollowingFragment : Fragment() {
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            if (intent != null && intent.action.equals(ApiCalls(context!!).LIKED_INTENT)) {
                val liked = intent.getStringArrayListExtra("liked")!!
                val size = liked.size
                val titles = Array(size) { "" }
                val portraitImages = Array(size) { "" }
                val landscapeImages = Array(size) { "" }
                val descriptions = Array(size) { "" }
                val seriesIds = Array(size) { "" }
                for (x in liked.indices) {
                    val json = JSONObject(liked[x])
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

                loading_f.visibility = View.GONE
            } else if (intent != null) {
                populateLiked(context!!)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LocalBroadcastManager.getInstance(context!!)
            .registerReceiver(receiver, IntentFilter(ApiCalls(context!!).LIKED_INTENT))

        LocalBroadcastManager.getInstance(context!!)
            .registerReceiver(receiver, IntentFilter(ApiCalls(context!!).UPDATE_LIKED))

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_following, container, false)
    }

    private fun populateLiked(context: Context) {
        val sharedPref = context.getSharedPreferences(
            context.resources.getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )

        val ids = sharedPref.getString("ids", "nope")

        if (!ids.equals("nope") && ids!!.length > 1) {
            val mIds = ids.split(",").toTypedArray()

            val apiCalls = ApiCalls(context)
            apiCalls.getLiked(mIds)
        } else {
            recyclerView.adapter = null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateLiked(view.context)
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(receiver)

        super.onDestroy()
    }
}

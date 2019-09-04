package io.github.joe2k01.animegratis


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_newest.*
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 */
class NewestFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_newest, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiCalls = ApiCalls()
        val newest = apiCalls.getNewest()

        val titles = Array(10) { "" }
        val portraitImages = Array(10) { "" }
        val landscapeImages = Array(10) { "" }
        val descriptions = Array(10) { "" }
        val seriesIds = Array(10) { "" }
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

        val linearLayoutManager = LinearLayoutManager(view.context)
        val animeAdapter = AnimeAdapter(
            view.context, titles, portraitImages, landscapeImages,
            descriptions, seriesIds
        )

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            adapter = animeAdapter
        }
    }
}

package io.github.joe2k01.animegratis


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
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

        val titles = arrayOfNulls<String>(10)
        val image = arrayOfNulls<String>(10)
        val seriesId = arrayOfNulls<String>(10)
        for (x in newest.indices) {
            val json = JSONObject(newest[x]!!)
            titles[x] = json.getString("name")
            seriesId[x] = json.getString("series_id")
            val portrait = JSONObject(json.getString("portrait_image"))
            image[x] = portrait.getString("medium_url")
        }

        val arrayAdapter =
            ArrayAdapter(activity!!.baseContext, android.R.layout.simple_list_item_1, titles)
        listView.adapter = arrayAdapter
    }

}

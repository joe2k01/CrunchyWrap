package io.github.joe2k01.crunchywrap

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.*
import kotlin.collections.ArrayList

class ApiCalls(val context: Context) {
    private lateinit var reqParam: String

    val authenticateIntent = "io.github.joe2k01.authenticate"
    val likedIntent = "io.github.joe2k01.following"
    val newestIntent = "io.github.joe2k01.newest"
    val episodesIntent = "io.github.joe2k01.episodes"
    val searchIntent = "io.github.joe2k01.search"
    val urlIntent = "io.github.joe2k01.url"

    val updateLiked = "io.github.joe2k01.update_liked"

    private val sharedPref: SharedPreferences = context.getSharedPreferences(
        context.resources.getString(R.string.preference_file_key), Context.MODE_PRIVATE
    )

    private fun clearParam() {
        reqParam = URLEncoder.encode(
            "device_id",
            "UTF-8"
        ) + "=" + URLEncoder.encode(
            sharedPref.getString("uuid", UUID.randomUUID().toString()),
            "UTF-8"
        )
        reqParam += "&" + URLEncoder.encode(
            "device_type",
            "UTF-8"
        ) + "=" + URLEncoder.encode("com.crunchyroll.crunchyroid", "UTF-8")
        reqParam += "&" + URLEncoder.encode(
            "access_token",
            "UTF-8"
        ) + "=" + URLEncoder.encode("WveH9VkPLrXvuNm", "UTF-8")
        reqParam += "&" + URLEncoder.encode("version", "UTF-8") + "=" + URLEncoder.encode(
            "444",
            "UTF-8"
        )
    }

    fun authenticate() {
        clearParam()
        val mURL = "https://api.crunchyroll.com/start_session.0.json?$reqParam"
        val queue = Volley.newRequestQueue(context)

        val request = StringRequest(
            Request.Method.GET, mURL,
            { response ->
                if (response.contains("session_id")) {
                    val dataObject = JSONObject(response.toString()).get("data")
                    val sessionId = JSONObject(dataObject.toString()).get("session_id").toString()

                    sharedPref.edit().putString("session_id", sessionId).apply()
                }
                val intent = Intent(authenticateIntent)
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            },
            null
        )

        queue.add(request)
    }

    fun getLocales() {
        clearParam()
        val sessionId = sharedPref.getString("session_id", "null")
        reqParam += "&" + URLEncoder.encode("session_id", "UTF-8") + "=" + URLEncoder.encode(
            sessionId,
            "UTF-8"
        )

        val mURL = "https://api.crunchyroll.com/list_locales.0.json?$reqParam"
        val queue = Volley.newRequestQueue(context)

        val request = StringRequest(
            Request.Method.GET, mURL,
            { response ->
                val dataObject = JSONObject(response.toString()).get("data").toString()
                val array = JSONArray(dataObject)
                sharedPref.edit().putString("locales", array.toString()).apply()
            },
            null
        )

        queue.add(request)
    }

    fun getNewest() {
        clearParam()
        val seriesArray = arrayOfNulls<String>(10)
        val sessionId = sharedPref.getString("session_id", "null")
        reqParam += "&" + URLEncoder.encode("session_id", "UTF-8") + "=" + URLEncoder.encode(
            sessionId,
            "UTF-8"
        )
        reqParam += "&" + URLEncoder.encode("media_type", "UTF-8") + "=" + URLEncoder.encode(
            "anime",
            "UTF-8"
        )

        reqParam += "&" + URLEncoder.encode("limit", "UTF-8") + "=" + URLEncoder.encode(
            "10",
            "UTF-8"
        )

        val mURL = "https://api.crunchyroll.com/list_series.0.json?$reqParam"
        val queue = Volley.newRequestQueue(context)

        val request = StringRequest(
            Request.Method.GET, mURL,
            { response ->
                val dataObject = JSONObject(response.toString()).get("data").toString()
                val array = JSONArray(dataObject)
                for (x in 0 until array.length()) {
                    seriesArray[x] = array.get(x).toString()
                }

                val intent = Intent(newestIntent)
                intent.putExtra("newest", seriesArray)
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            },
            null
        )

        queue.add(request)
    }

    fun getLiked(ids: Array<String>) {
        clearParam()
        val seriesArray = ArrayList<String>()
        val sessionId = sharedPref.getString("session_id", "null")
        val originalParam = reqParam

        val queue = Volley.newRequestQueue(context)

        for (x in 0..(ids.size - 2)) {
            reqParam = originalParam
            reqParam += "&" + URLEncoder.encode(
                "session_id",
                "UTF-8"
            ) + "=" + URLEncoder.encode(
                sessionId,
                "UTF-8"
            )
            reqParam += "&" + URLEncoder.encode(
                "series_id",
                "UTF-8"
            ) + "=" + URLEncoder.encode(
                ids[x],
                "UTF-8"
            )

            val mURL = "https://api.crunchyroll.com/info.0.json?$reqParam"
            val request = StringRequest(
                Request.Method.GET, mURL,
                { response ->
                    seriesArray.add(JSONObject(response).get("data").toString())

                    val intent = Intent(likedIntent)
                    intent.putExtra("liked", seriesArray)
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
                },
                null
            )

            queue.add(request)
        }
    }

    fun search(query: String) {
        clearParam()
        val seriesArray = ArrayList<String>()
        val sessionId = sharedPref.getString("session_id", "null")
        reqParam += "&" + URLEncoder.encode("session_id", "UTF-8") + "=" + URLEncoder.encode(
            sessionId,
            "UTF-8"
        )
        reqParam += "&" + URLEncoder.encode("media_types", "UTF-8") + "=" + URLEncoder.encode(
            "anime",
            "UTF-8"
        )

        reqParam += "&" + URLEncoder.encode("q", "UTF-8") + "=" + URLEncoder.encode(
            query,
            "UTF-8"
        )

        val mURL = "https://api.crunchyroll.com/autocomplete.0.json?$reqParam"
        val queue = Volley.newRequestQueue(context)

        val request = StringRequest(
            Request.Method.GET, mURL,
            { response ->
                val dataObject = JSONObject(response.toString()).get("data").toString()
                val array = JSONArray(dataObject)
                for (x in 0 until array.length()) {
                    seriesArray.add(array.get(x).toString())
                }

                val intent = Intent(searchIntent)
                intent.putExtra("results", seriesArray)
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            },
            null
        )

        queue.add(request)
    }

    fun getEpisodes(seriesId: String) {
        clearParam()
        val episodes = ArrayList<String>()
        val sessionId = sharedPref.getString("session_id", "null")

        reqParam += "&" + URLEncoder.encode("session_id", "UTF-8") + "=" + URLEncoder.encode(
            sessionId,
            "UTF-8"
        )
        reqParam += "&" + URLEncoder.encode("series_id", "UTF-8") + "=" + URLEncoder.encode(
            seriesId,
            "UTF-8"
        )
        reqParam += "&" + URLEncoder.encode("limit", "UTF-8") + "=" + URLEncoder.encode(
            Int.MAX_VALUE.toString(),
            "UTF-8"
        )

        val mURL = "https://api.crunchyroll.com/list_media.0.json?$reqParam"
        val queue = Volley.newRequestQueue(context)

        val request = StringRequest(
            Request.Method.GET, mURL,
            { response ->
                val dataObject = JSONObject(response.toString()).get("data").toString()
                val array = JSONArray(dataObject)
                for (x in 0 until array.length()) {
                    episodes.add(array.get(x).toString())
                }

                val intent = Intent(episodesIntent)
                intent.putExtra("episodes", episodes)
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            },
            null
        )

        queue.add(request)
    }

    fun getStreamingLink(mediaId: String, locale: String) {
        clearParam()
        var url = ""
        val sessionId = sharedPref.getString("session_id", "null")
        reqParam += "&" + URLEncoder.encode("session_id", "UTF-8") + "=" + URLEncoder.encode(
            sessionId,
            "UTF-8"
        )

        if (locale != "") {
            reqParam += "&" + URLEncoder.encode("locale", "UTF-8") + "=" + URLEncoder.encode(
                locale,
                "UTF-8"
            )
        }

        reqParam += "&" + URLEncoder.encode("media_id", "UTF-8") + "=" + URLEncoder.encode(
            mediaId,
            "UTF-8"
        )
        reqParam += "&" + URLEncoder.encode("fields", "UTF-8") + "=" + URLEncoder.encode(
            "media.stream_data",
            "UTF-8"
        )

        val mURL = "https://api.crunchyroll.com/info.0.json?$reqParam"
        val queue = Volley.newRequestQueue(context)

        val request = StringRequest(
            Request.Method.GET, mURL,
            { response ->
                if (response.toString().contains("url")) {
                    val dataObject = JSONObject(response.toString()).get("data").toString()
                    val streamData = JSONObject(dataObject).get("stream_data").toString()
                    val streams = JSONObject(streamData).get("streams").toString()
                    val array = JSONArray(streams)
                    if (array.length() > 0) {
                        val adaptiveObject = JSONObject(array.get(0).toString())
                        url = adaptiveObject.getString("url")
                    }
                }

                val intent = Intent(urlIntent)
                intent.putExtra("url", url)
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            },
            {
                val intent = Intent(urlIntent)
                intent.putExtra("url", url)
                LocalBroadcastManager.getInstance(context)
                    .sendBroadcast(intent)  // Send an empty url in case the request errors out
            }
        )

        queue.add(request)
    }
}
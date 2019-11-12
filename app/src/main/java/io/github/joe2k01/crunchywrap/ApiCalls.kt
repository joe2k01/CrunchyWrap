package io.github.joe2k01.crunchywrap

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*
import kotlin.collections.ArrayList

class ApiCalls(val context: Context) {
    lateinit var reqParam: String

    val LIKED_INTENT = "io.github.joe2k01.following"
    val NEWEST_INTENT = "io.github.joe2k01.newest"
    val EPISODES_INTENT = "io.github.joe2k01.episodes"
    val SEARCH_INTENT = "io.github.joe2k01.search"
    val URL_INTENT = "io.github.joe2k01.url"

    val UPDATE_LIKED = "io.github.joe2k01.update_liked"

    private val sharedPref: SharedPreferences = context.getSharedPreferences(
        context.resources.getString(R.string.preference_file_key), Context.MODE_PRIVATE
    )

    fun clearParam() {
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
        ) + "=" + URLEncoder.encode("Scwg9PRRZ19iVwD", "UTF-8")
        reqParam += "&" + URLEncoder.encode("version", "UTF-8") + "=" + URLEncoder.encode(
            "444",
            "UTF-8"
        )
    }

    fun authenticate() {
        clearParam()
        var sessionId = ""
        val t = Thread {
            val mURL = URL("https://api.crunchyroll.com/start_session.0.json")

            with(mURL.openConnection() as HttpURLConnection) {
                requestMethod = "POST"

                val wr = OutputStreamWriter(outputStream)
                wr.write(reqParam)
                wr.flush()

                BufferedReader(InputStreamReader(inputStream)).use {
                    val response = StringBuffer()

                    var inputLine = it.readLine()
                    while (inputLine != null) {
                        response.append(inputLine)
                        inputLine = it.readLine()
                    }
                    it.close()
                    val dataObject = JSONObject(response.toString()).get("data")
                    sessionId = JSONObject(dataObject.toString()).get("session_id").toString()
                }
            }

            with(sharedPref.edit()) {
                putString("session_id", sessionId)
                apply()
            }
        }

        t.start()
        t.join()
    }

    fun getNewest() {
        clearParam()
        var seriesArray = arrayOfNulls<String>(10)
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

        val t = Thread {
            val mURL = URL("https://api.crunchyroll.com/list_series.0.json")

            with(mURL.openConnection() as HttpURLConnection) {
                requestMethod = "POST"

                val wr = OutputStreamWriter(outputStream)
                wr.write(reqParam)
                wr.flush()

                BufferedReader(InputStreamReader(inputStream)).use {
                    val response = StringBuffer()

                    var inputLine = it.readLine()
                    while (inputLine != null) {
                        response.append(inputLine)
                        inputLine = it.readLine()
                    }
                    it.close()
                    val dataObject = JSONObject(response.toString()).get("data").toString()
                    val array = JSONArray(dataObject)
                    for (x in 0 until array.length()) {
                        seriesArray[x] = array.get(x).toString()
                    }
                }
            }

            val intent = Intent(NEWEST_INTENT)
            intent.putExtra("newest", seriesArray)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }
        t.start()
    }

    fun getLiked(ids: Array<String>) {
        clearParam()
        var seriesArray = ArrayList<String>()
        val sessionId = sharedPref.getString("session_id", "null")
        val originalParam = reqParam

        val t = Thread {
            val mURL = URL("https://api.crunchyroll.com/info.0.json")

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

                with(mURL.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"

                    val wr = OutputStreamWriter(outputStream)
                    wr.write(reqParam)
                    wr.flush()

                    BufferedReader(InputStreamReader(inputStream)).use {
                        val response = StringBuffer()

                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            response.append(inputLine)
                            inputLine = it.readLine()
                        }
                        it.close()
                        seriesArray.add(JSONObject(response.toString()).get("data").toString())
                    }

                    val intent = Intent(LIKED_INTENT)
                    intent.putExtra("liked", seriesArray)
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
                }
            }
        }
        t.start()
    }

    fun search(query: String) {
        clearParam()
        var seriesArray = ArrayList<String>()
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

        val t = Thread {
            val mURL = URL("https://api.crunchyroll.com/autocomplete.0.json")

            with(mURL.openConnection() as HttpURLConnection) {
                requestMethod = "POST"

                val wr = OutputStreamWriter(outputStream)
                wr.write(reqParam)
                wr.flush()

                BufferedReader(InputStreamReader(inputStream)).use {
                    val response = StringBuffer()

                    var inputLine = it.readLine()
                    while (inputLine != null) {
                        response.append(inputLine)
                        inputLine = it.readLine()
                    }
                    it.close()
                    val dataObject = JSONObject(response.toString()).get("data").toString()
                    val array = JSONArray(dataObject)
                    for (x in 0 until array.length()) {
                        seriesArray.add(array.get(x).toString())
                    }
                }

                val intent = Intent(SEARCH_INTENT)
                intent.putExtra("results", seriesArray)
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            }
        }
        t.start()
    }

    fun getEpisodes(seriesId: String) {
        clearParam()
        var episodes = ArrayList<String>()
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

        val t = Thread {
            val mURL = URL("https://api.crunchyroll.com/list_media.0.json")

            with(mURL.openConnection() as HttpURLConnection) {
                requestMethod = "POST"

                val wr = OutputStreamWriter(outputStream)
                wr.write(reqParam)
                wr.flush()

                BufferedReader(InputStreamReader(inputStream)).use {
                    val response = StringBuffer()

                    var inputLine = it.readLine()
                    while (inputLine != null) {
                        response.append(inputLine)
                        inputLine = it.readLine()
                    }
                    it.close()
                    val dataObject = JSONObject(response.toString()).get("data").toString()
                    val array = JSONArray(dataObject)
                    for (x in 0 until array.length()) {
                        episodes.add(array.get(x).toString())
                    }
                }
            }

            val intent = Intent(EPISODES_INTENT)
            intent.putExtra("episodes", episodes)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }
        t.start()
    }

    fun getStreamingLink(mediaId: String) {
        clearParam()
        var url = ""
        var sessionId = sharedPref.getString("session_id", "null")
        reqParam += "&" + URLEncoder.encode("session_id", "UTF-8") + "=" + URLEncoder.encode(
            sessionId,
            "UTF-8"
        )
        reqParam += "&" + URLEncoder.encode("media_id", "UTF-8") + "=" + URLEncoder.encode(
            mediaId,
            "UTF-8"
        )
        reqParam += "&" + URLEncoder.encode("fields", "UTF-8") + "=" + URLEncoder.encode(
            "media.stream_data",
            "UTF-8"
        )

        val t = Thread {
            val mURL = URL("https://api.crunchyroll.com/info.0.json")

            with(mURL.openConnection() as HttpURLConnection) {
                requestMethod = "POST"

                val wr = OutputStreamWriter(outputStream)
                wr.write(reqParam)
                wr.flush()

                BufferedReader(InputStreamReader(inputStream)).use {
                    val response = StringBuffer()

                    var inputLine = it.readLine()
                    while (inputLine != null) {
                        response.append(inputLine)
                        inputLine = it.readLine()
                    }
                    it.close()
                    val dataObject = JSONObject(response.toString()).get("data").toString()
                    val streamData = JSONObject(dataObject).get("stream_data").toString()
                    val streams = JSONObject(streamData).get("streams").toString()
                    val array = JSONArray(streams)
                    val adaptiveObject = JSONObject(array.get(0).toString())
                    url = adaptiveObject.getString("url")
                }
            }

            val intent = Intent(URL_INTENT)
            intent.putExtra("url", url)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }
        t.start()
    }
}
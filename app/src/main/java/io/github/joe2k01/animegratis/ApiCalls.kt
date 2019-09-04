package io.github.joe2k01.animegratis

import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class ApiCalls {
    lateinit var reqParam: String

    fun authenticate(): String {

        var sessionId = ""
        val t = Thread {
            reqParam = URLEncoder.encode(
                "device_id",
                "UTF-8"
            ) + "=" + URLEncoder.encode("efad739f-d50d-42d6-a504-f1af339393ff", "UTF-8")
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
        }

        t.start()
        t.join()

        return sessionId
    }

    fun getNewest(): Array<String?> {
        var seriesArray = arrayOfNulls<String>(10)
        val sessionId = authenticate()
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
        }
        t.start()
        t.join()

        return seriesArray
    }

    fun getEpisodes(seriesId: String): ArrayList<String> {
        var episodes = ArrayList<String>()
        val sessionId = authenticate()

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
        }
        t.start()
        t.join()

        return episodes
    }

    fun getStreamingLink(mediaId: String): String {
        var url = ""
        var sessionId = authenticate()
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
        }
        t.start()
        t.join()

        return url
    }
}
package io.github.joe2k01.animegratis

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class AnimeAdapter(
    private val context: Context,
    private val titles: Array<String>,
    private val portraitImages: Array<String>,
    private val landscapeImages: Array<String>,
    private val descriptions: Array<String>,
    private val serieIds: Array<String>
) : RecyclerView.Adapter<AnimeAdapter.AnimeViewHolder>() {

    class AnimeViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private val metrics: DisplayMetrics = context.resources.displayMetrics
    private val sharedPref = context.getSharedPreferences(
        context.resources.getString(R.string.preference_file_key), Context.MODE_PRIVATE
    )
    private var liked = sharedPref.getString("ids", "none")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val layout =
            LayoutInflater.from(parent.context).inflate(R.layout.anime_list_item, parent, false)

        return AnimeViewHolder(layout)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val title = holder.view.findViewById<TextView>(R.id.title)
        val image = holder.view.findViewById<ImageView>(R.id.image)
        val like = holder.view.findViewById<ImageView>(R.id.like)
        val item = holder.view.findViewById<LinearLayout>(R.id.item)

        var alreadyLiked = liked!!.contains(serieIds[position])

        val hContext = holder.view.context

        if (hContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val width = metrics.widthPixels / 6
            image.layoutParams.width = width
            image.layoutParams.height = 300 * width / 200
        } else {
            val width = metrics.widthPixels / 3
            image.layoutParams.width = width
            image.layoutParams.height = 300 * width / 200
        }

        title.text = titles[position]

        if (alreadyLiked)
            like.setImageDrawable(hContext.resources.getDrawable(R.drawable.ic_thumb_up))
        else
            like.setImageDrawable(hContext.resources.getDrawable(R.drawable.ic_thumb_up_outline))

        Picasso.get()
            .load(portraitImages[position])
            .fit()
            .into(image)

        like.setOnClickListener {
            alreadyLiked = liked!!.contains(serieIds[position])
            if (!alreadyLiked) {
                like.setImageDrawable(hContext.resources.getDrawable(R.drawable.ic_thumb_up))

                val animeId = serieIds[position]

                if (liked.equals("none")) {
                    with(sharedPref.edit()) {
                        putString("ids", "$animeId,")
                        apply()
                    }
                } else {
                    with(sharedPref.edit()) {
                        putString("ids", "$animeId,$liked")
                        apply()
                    }
                }

                liked = sharedPref.getString("ids", "none")
            } else {
                like.setImageDrawable(hContext.resources.getDrawable(R.drawable.ic_thumb_up_outline))

                val animeId = serieIds[position]

                with(sharedPref.edit()) {
                    putString("ids", liked!!.replace("$animeId,", ""))
                    apply()
                }

                liked = sharedPref.getString("ids", "none")
            }

            val intent = Intent(ApiCalls(context).UPDATE_LIKED)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }

        item.setOnClickListener {
            val anime = Intent(hContext, AnimeActivity::class.java)
            anime.putExtra("id", serieIds[position])
            anime.putExtra("title", titles[position])
            anime.putExtra("description", descriptions[position])
            anime.putExtra("image", landscapeImages[position])
            hContext.startActivity(anime)
        }
    }

    override fun getItemCount() = titles.size
}
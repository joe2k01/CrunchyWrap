package io.github.joe2k01.crunchywrap

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
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class EpisodesAdapter(
    private val context: Context,
    private val titles: Array<String>,
    private val numbers: Array<String>,
    private val images: Array<String>,
    private val mediaIds: Array<String>
) : RecyclerView.Adapter<EpisodesAdapter.EpisodesViewHolder>() {

    class EpisodesViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private val metrics: DisplayMetrics = context.resources.displayMetrics
    private val sharedPreferences =
        context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodesViewHolder {
        val layout =
            LayoutInflater.from(parent.context).inflate(R.layout.episodes_list_item, parent, false)

        return EpisodesViewHolder(layout)
    }

    override fun onBindViewHolder(holder: EpisodesViewHolder, position: Int) {
        val title = holder.view.findViewById<TextView>(R.id.title)
        val image = holder.view.findViewById<ImageView>(R.id.image)
        val item = holder.view.findViewById<LinearLayout>(R.id.item)

        if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val width = metrics.widthPixels / 6
            image.layoutParams.width = width
            image.layoutParams.height = 360 * width / 640
        } else {
            val width = metrics.widthPixels / 3
            image.layoutParams.width = width
            image.layoutParams.height = 360 * width / 640
        }

        title.text = String.format(
            context.getString(R.string.episodes_placeholder),
            numbers[position],
            titles[position]
        )

        Picasso.get()
            .load(images[position])
            .fit()
            .into(image)

        item.setOnClickListener {
            val episode = Intent(holder.view.context, StreamingActivity::class.java)
            episode.putExtra("id", mediaIds[position])
            episode.putExtra("locale", sharedPreferences.getString("locale", ""))
            holder.view.context.startActivity(episode)
        }
    }

    override fun getItemCount() = titles.size
}
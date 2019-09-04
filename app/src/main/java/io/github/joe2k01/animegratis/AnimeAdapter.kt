package io.github.joe2k01.animegratis

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class AnimeAdapter(
    private val context: Context?,
    private val titles: Array<String>,
    private val portraitImages: Array<String>,
    private val landscapeImages: Array<String>,
    private val descriptions: Array<String>,
    private val serieIds: Array<String>
) : RecyclerView.Adapter<AnimeAdapter.AnimeViewHolder>() {

    class AnimeViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val layout =
            LayoutInflater.from(parent.context).inflate(R.layout.anime_list_item, parent, false)

        return AnimeViewHolder(layout)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val title = holder.view.findViewById<TextView>(R.id.title)
        val image = holder.view.findViewById<ImageView>(R.id.image)
        val item = holder.view.findViewById<LinearLayout>(R.id.item)

        title.text = titles[position]

        Picasso.get()
            .load(portraitImages[position])
            .fit()
            .into(image)

        item.setOnClickListener {
            val anime = Intent(context, AnimeActivity::class.java)
            anime.putExtra("id", serieIds[position])
            anime.putExtra("title", titles[position])
            anime.putExtra("description", descriptions[position])
            anime.putExtra("image", landscapeImages[position])
            context?.startActivity(anime)
        }
    }

    override fun getItemCount() = titles.size
}
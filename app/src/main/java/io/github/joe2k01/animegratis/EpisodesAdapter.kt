package io.github.joe2k01.animegratis

import android.content.Context
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
    private val context: Context?,
    private val titles: Array<String>,
    private val numbers: Array<String>,
    private val images: Array<String>,
    private val mediaIds: Array<String>
) : RecyclerView.Adapter<EpisodesAdapter.EpisodesViewHolder>() {

    class EpisodesViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private val metrics: DisplayMetrics = context!!.resources.displayMetrics
    private val width: Int = metrics.widthPixels / 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodesViewHolder {
        val layout =
            LayoutInflater.from(parent.context).inflate(R.layout.episodes_list_item, parent, false)

        return EpisodesViewHolder(layout)
    }

    override fun onBindViewHolder(holder: EpisodesViewHolder, position: Int) {
        val title = holder.view.findViewById<TextView>(R.id.title)
        val image = holder.view.findViewById<ImageView>(R.id.image)
        val item = holder.view.findViewById<LinearLayout>(R.id.item)

        title.text = String.format(
            context!!.getString(R.string.episodes_placeholder),
            numbers[position],
            titles[position]
        )

        image.layoutParams.width = width
        image.layoutParams.height = 360 * width / 640

        Picasso.get()
            .load(images[position])
            .fit()
            .into(image)
    }

    override fun getItemCount() = (titles.size - 1)
}
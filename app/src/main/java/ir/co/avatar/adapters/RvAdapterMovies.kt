package ir.co.avatar.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import ir.co.avatar.R
import ir.co.common.dto.MovieModel
import kotlinx.android.synthetic.main.item_movie.view.*


class RvAdapterMovies(private val onItemSelected: (MovieModel) -> Unit) :
    PagedListAdapter<MovieModel, RvAdapterMovies.TransactionHolder>(MovieModel.DiffUtils()) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): TransactionHolder {
        context = parent.context
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)

        return TransactionHolder(view)
    } // onCreateViewHolder

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        getItem(position).let {
            if (it != null) holder.bindData(it)
        } // let
    } // onBindViewHolder

    inner class TransactionHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bindData(item: MovieModel) {
            view.ivMovie.load(item.poster)
            view.tvTitle.text = item.title
            view.tvDescription.text = item.type
            view.tvRank.text = item.year

            itemView.setOnClickListener {
                onItemSelected(item)
            }
        } // bindData
    } // TransactionHolder
} // RvAdapterMovies
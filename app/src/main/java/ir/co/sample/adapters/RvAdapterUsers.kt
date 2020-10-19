package ir.co.sample.adapters


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import ir.co.common.dto.User
import ir.co.sample.R
import kotlinx.android.synthetic.main.item_user.view.*


class RvAdapterUsers(private val onItemSelected: (User, ImageView, TextView) -> Unit) :
    PagedListAdapter<User, RvAdapterUsers.TransactionHolder>(User.DiffUtils()) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): TransactionHolder {
        context = parent.context
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)

        return TransactionHolder(view)
    } // onCreateViewHolder

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        getItem(position).let {
            if (it != null) holder.bindData(it)
        } // let
    } // onBindViewHolder

    inner class TransactionHolder(val view: View) : RecyclerView.ViewHolder(view) {

        @SuppressLint("SetTextI18n")
        fun bindData(item: User) {
            view.ivPhoto.load(item.avatar)
            view.tvTitle.text = "${item.firstName}  ${item.lastName}"
            view.tvDescription.text = item.email

            itemView.setOnClickListener {
                onItemSelected(item, view.ivPhoto, view.tvTitle)
            }
        } // bindData
    } // TransactionHolder
} // RvAdapterMovies
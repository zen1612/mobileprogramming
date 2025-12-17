package com.example.mobileprogrammingfinals

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class rvAdapter(val items: MutableList<BookModel>) : RecyclerView.Adapter<rvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): rvAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item, parent, false)
        return ViewHolder(view)
    }

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null

    override fun onBindViewHolder(holder: rvAdapter.ViewHolder, position: Int) {
        if (itemClick != null) {
            holder.itemView.setOnClickListener { v -> itemClick?.onClick(v, position) }
        }
        holder.bindItems(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: BookModel) {
            val bookTitle = itemView.findViewById<TextView>(R.id.bookTitle)
            val bookAuthor = itemView.findViewById<TextView>(R.id.bookAuthor)
            val bookCategory = itemView.findViewById<TextView>(R.id.bookCategory)
            //val status = itemView.findViewById<TextView>(R.id.status)

            bookTitle.text = item.bookTitle
            bookAuthor.text = "by ${item.bookAuthor}"
            bookCategory.text = "Genre: ${item.rating}"

        }
    }
}

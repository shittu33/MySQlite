package com.example.sample.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.sample.R
import com.example.sample.adapter.NoteAdapter.UserHolder
import com.example.sample.tables.Note

class NoteAdapter() : ListAdapter<Note, UserHolder>(DIFF_CALLBACK) {
    private var mUserHolder: UserHolder? = null
    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<Note> =
            object : DiffUtil.ItemCallback<Note>() {
                override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                    return oldItem.author == newItem.author
                            && oldItem.details == newItem.details
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        mUserHolder = UserHolder(itemView)
        return mUserHolder!!
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        holder.populateData(getItem(position))
    }

    fun getUser(pos: Int): Note? {
        return getItem(pos)
    }

    inner class UserHolder(itemView: View) : ViewHolder(itemView) {
        private val tvId: TextView = itemView.findViewById(R.id.tv_id)
        private val tvAuthor: TextView = itemView.findViewById(R.id.tv_author)
        private val tvDetails: TextView = itemView.findViewById(R.id.tv_details)

        init {
            itemView.setOnClickListener { v ->
                val current_pos = adapterPosition
                if (mItemListener != null) {
                    mItemListener!!.onClick(v, getUser(current_pos), current_pos)
                }
            }
        }

        fun populateData(note: Note?) {
            tvId.text = note!!.id.toString()
            tvAuthor.text = note.author
            tvDetails.text = note.details
        }
    }

    private var mItemListener: ItemListener? = null
    fun setItemListener(mItemListener: ItemListener?) {
        this.mItemListener = mItemListener
    }

    interface ItemListener {
        fun onClick(v: View?, Note: Note?, pos: Int)
    }

}
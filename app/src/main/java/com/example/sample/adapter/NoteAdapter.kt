package com.example.sample.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.sample.R
import com.example.sample.adapter.NoteAdapter.NoteHolder
import com.example.sample.tables.Note
import kotlinx.android.synthetic.main.note_item.view.*

class NoteAdapter() : ListAdapter<Note, NoteHolder>(DIFF_CALLBACK) {
    private var mNoteHolder: NoteHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        mNoteHolder = NoteHolder(itemView)
        Log.e(TAG, "onCreateViewHolder")
        return mNoteHolder!!
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        Log.e(TAG, "onBindViewHolder")
        holder.populateData(getItem(position))
    }

    fun getNote(pos: Int): Note? {
        return getItem(pos)
    }

    inner class NoteHolder(itemView: View) : ViewHolder(itemView) {
        private val tvId: TextView = itemView.tv_id
        private val tvAuthor: TextView = itemView.tv_author
        private val tvDetails: TextView = itemView.tv_details

        init {
            itemView.setOnClickListener { v ->
                val currentPos = adapterPosition
                if (mItemListener != null) {
                    mItemListener!!.onClick(v, getNote(currentPos), currentPos)
                }
            }
        }

        fun populateData(note: Note?) {
            tvId.text = note!!.id.toString()
            tvAuthor.text = note.author
            tvDetails.text = note.details
            Log.e(TAG, "note populated")
        }
    }

    private var mItemListener: ItemListener? = null
    fun setItemListener(mItemListener: ItemListener?) {
        this.mItemListener = mItemListener
    }

    interface ItemListener {
        fun onClick(v: View?, Note: Note?, pos: Int)
    }

    companion object {
        const val TAG: String = "NoteAdapter"
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

}
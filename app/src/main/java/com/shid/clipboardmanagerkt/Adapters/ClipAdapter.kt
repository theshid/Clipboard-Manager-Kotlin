package com.shid.clipboardmanagerkt.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shid.clipboardmanagerkt.Model.ClipEntry
import com.shid.clipboardmanagerkt.R
import java.text.SimpleDateFormat
import java.util.*

class ClipAdapter constructor(var context: Context, var listener: ItemClickListener) :
    RecyclerView.Adapter<ClipAdapter.ClipViewHolder>() {

    private var mClipEntries = listOf<ClipEntry>()
    set(value){
        field = value
        notifyDataSetChanged()
    }
    private lateinit var mContext: Context
    private var dateFormat: SimpleDateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())

    init {
        mContext = context
    }

    companion object {
        val DATE_FORMAT: String = "dd/MM/yyyy"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClipViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.clip_layout, parent, false)

        return ClipViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mClipEntries.size
    }

    override fun onBindViewHolder(holder: ClipViewHolder, position: Int) {
        val clipEntry: ClipEntry = mClipEntries[position]
        val clip: String = clipEntry.entry
        val date: String = dateFormat.format(clipEntry.date)
        val clip_id: Int = clipEntry.clipId

        //Set values

        //Set values
        holder.clipView.text = clip
        holder.dateClip.text = date

    }

    fun getClipEntries(): List<ClipEntry> {
        return mClipEntries
    }

    /**
     * When data changes, this method updates the list of clipEntries
     * and notifies the adapter to use the new values on it
     */

    fun setClips(clipEntries: List<ClipEntry>) {
        mClipEntries = clipEntries
        notifyDataSetChanged()
    }

    interface ItemClickListener {
        fun onItemClickListener(itemId: Int)
    }

    class ClipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        lateinit var clipView: TextView
        lateinit var dateClip: TextView

        init {
            clipView = itemView.findViewById(R.id.clip_entry);
            dateClip = itemView.findViewById(R.id.clipDate);
        }


    }


}
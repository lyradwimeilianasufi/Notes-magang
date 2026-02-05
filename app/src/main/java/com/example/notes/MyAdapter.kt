package com.example.notes

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.notes.data.Note

class MyAdapter(private val context: Context, private val values: MutableList<Note>) :
    ArrayAdapter<Note>(context, R.layout.list_item, values) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView: View = inflater.inflate(R.layout.list_item, parent, false)

        val titleTextView = rowView.findViewById<TextView>(R.id.tvTitle)
        val contentTextView = rowView.findViewById<TextView>(R.id.tvContent)

        val note = values[position]
        titleTextView.text = note.title
        contentTextView.text = note.content

        rowView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("NOTE_ID", note.id)
            intent.putExtra("NOTE_TITLE", note.title)
            intent.putExtra("NOTE_CONTENT", note.content)
            context.startActivity(intent)
        }

        return rowView
    }
}

package com.example.notes

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class MyAdapter(private val context: Context, private val values: MutableList<String>) :
    ArrayAdapter<String?>(context, R.layout.list_item, values as List<String?>) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView: View = inflater.inflate(R.layout.list_item, parent, false)

        val titleTextView = rowView.findViewById<TextView>(R.id.tvTitle)
        val contentTextView = rowView.findViewById<TextView>(R.id.tvContent)

        titleTextView.text = "Note ${position + 1}"
        contentTextView.text = values[position]

        rowView.setOnClickListener { // Start DetailActivity and pass the item data
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("ITEM", values[position])
            context.startActivity(intent)
        }

        return rowView
    }
}
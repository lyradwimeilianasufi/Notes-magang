package com.example.notes

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.notes.data.Note
import com.example.notes.data.NoteDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private var adapter: MyAdapter? = null
    private var items = mutableListOf<Note>()
    private lateinit var database: NoteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = NoteDatabase.getDatabase(this)
        val listView = findViewById<ListView>(R.id.listView)
        val addNoteButton = findViewById<FloatingActionButton>(R.id.fabAdd)

        adapter = MyAdapter(this, items)
        listView.adapter = adapter

        addNoteButton.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_NOTE)
        }
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }

    private fun loadNotes() {
        lifecycleScope.launch {
            val notes = database.noteDao().getAllNotes()
            items.clear()
            items.addAll(notes)
            adapter?.notifyDataSetChanged()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
            val title = data?.getStringExtra("NOTE_TITLE") ?: ""
            val content = data?.getStringExtra("NOTE_CONTENT") ?: ""
            
            lifecycleScope.launch {
                val newNote = Note(title = title, content = content)
                database.noteDao().insert(newNote)
                loadNotes()
            }
        }
    }

    companion object {
        const val REQUEST_CODE_ADD_NOTE: Int = 1
    }
}
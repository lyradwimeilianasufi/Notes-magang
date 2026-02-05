package com.example.notes

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.SearchView
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
    private var allNotes = mutableListOf<Note>()
    private var displayedItems = mutableListOf<Note>()
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
        val searchView = findViewById<SearchView>(R.id.searchView)

        adapter = MyAdapter(this, displayedItems)
        listView.adapter = adapter

        addNoteButton.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_NOTE)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterNotes(newText)
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }

    private fun loadNotes() {
        lifecycleScope.launch {
            val notes = database.noteDao().getAllNotes()
            allNotes.clear()
            allNotes.addAll(notes)
            filterNotes(null) // Reset filter when loading
        }
    }

    private fun filterNotes(query: String?) {
        displayedItems.clear()
        if (query.isNullOrEmpty()) {
            displayedItems.addAll(allNotes)
        } else {
            val lowerCaseQuery = query.lowercase()
            for (note in allNotes) {
                if (note.title.lowercase().contains(lowerCaseQuery) ||
                    note.content.lowercase().contains(lowerCaseQuery)
                ) {
                    displayedItems.add(note)
                }
            }
        }
        adapter?.notifyDataSetChanged()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
            val title = data?.getStringExtra("NOTE_TITLE") ?: ""
            val content = data?.getStringExtra("NOTE_CONTENT") ?: ""
            val isFavorite = data?.getBooleanExtra("NOTE_FAVORITE", false) ?: false
            
            lifecycleScope.launch {
                val newNote = Note(title = title, content = content, isFavorite = isFavorite)
                database.noteDao().insert(newNote)
                loadNotes()
            }
        }
    }

    companion object {
        const val REQUEST_CODE_ADD_NOTE: Int = 1
    }
}
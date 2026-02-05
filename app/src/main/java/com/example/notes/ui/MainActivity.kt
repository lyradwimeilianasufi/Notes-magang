package com.example.notes.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.notes.R
import com.example.notes.data.Note
import com.example.notes.viewmodel.NoteViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {
    private var adapter: MyAdapter? = null
    private val viewModel: NoteViewModel by viewModels()
    private var allNotesList = mutableListOf<Note>()
    private var displayedItems = mutableListOf<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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

        // MVVM: Observasi data dari ViewModel
        viewModel.allNotes.observe(this) { notes ->
            allNotesList.clear()
            allNotesList.addAll(notes)
            filterNotes(searchView.query.toString())
        }
    }

    private fun filterNotes(query: String?) {
        displayedItems.clear()
        if (query.isNullOrEmpty()) {
            displayedItems.addAll(allNotesList)
        } else {
            val lowerCaseQuery = query.lowercase()
            for (note in allNotesList) {
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
            
            val newNote = Note(title = title, content = content, isFavorite = isFavorite)
            viewModel.insert(newNote) // MVVM: Simpan lewat ViewModel
        }
    }

    companion object {
        const val REQUEST_CODE_ADD_NOTE: Int = 1
    }
}
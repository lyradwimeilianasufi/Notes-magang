package com.example.notes.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.data.Note
import com.example.notes.databinding.ActivityMainBinding
import com.example.notes.viewmodel.NoteViewModel
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton


class MainActivity : AppCompatActivity() {
    private lateinit var adapter: MyAdapter
    private val viewModel: NoteViewModel by viewModels()
    private var allNotesList = listOf<Note>()
    private var displayedItems = mutableListOf<Note>()
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val addNoteButton = findViewById<ExtendedFloatingActionButton>(R.id.fabAdd)
        val searchView = findViewById<SearchView>(R.id.searchView)

        // Setup RecyclerView
        adapter = MyAdapter(displayedItems) { note ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("NOTE_ID", note.id)
            intent.putExtra("NOTE_TITLE", note.title)
            intent.putExtra("NOTE_CONTENT", note.content)
            intent.putExtra("NOTE_FAVORITE", note.isFavorite)
            intent.putExtra("NOTE_TIMESTAMP", note.timestamp)
            startActivity(intent)
        }

        binding.apply {

        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        recyclerView.adapter = adapter

        addNoteButton.setOnClickListener {
            val intent = Intent(this@MainActivity, AddNoteActivity::class.java)
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
        viewModel.allNotes.observe(this@MainActivity) { notes ->
            allNotesList = notes
            filterNotes(searchView.query.toString())
        }

        // Animasi Extended FAB saat scroll
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) addNoteButton.shrink()
                else if (dy < 0) addNoteButton.extend()
            }
        })
        }

    }


    private fun filterNotes(query: String?) {
        val filteredList = if (query.isNullOrEmpty()) {
            allNotesList
        } else {
            val lowerCaseQuery = query.lowercase()
            allNotesList.filter {
                it.title.lowercase().contains(lowerCaseQuery) ||
                        it.content.lowercase().contains(lowerCaseQuery)
            }
        }

        displayedItems.clear()
        displayedItems.addAll(filteredList)
        adapter.updateData(displayedItems)

        // Update Empty State visibility
        if (displayedItems.isEmpty()) {
            binding.emptyState.root.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.emptyState.root.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
            val title = data?.getStringExtra("NOTE_TITLE") ?: ""
            val content = data?.getStringExtra("NOTE_CONTENT") ?: ""
            val isFavorite = data?.getBooleanExtra("NOTE_FAVORITE", false) ?: false

            val newNote = Note(title = title, content = content, isFavorite = isFavorite)
            viewModel.insert(newNote)
        }
    }

    companion object {
        const val REQUEST_CODE_ADD_NOTE: Int = 1
    }
}
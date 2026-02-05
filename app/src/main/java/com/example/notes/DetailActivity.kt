package com.example.notes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.notes.data.Note
import com.example.notes.data.NoteDatabase
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {
    private var noteId: Int = -1
    private lateinit var tvDetailTitle: TextView
    private lateinit var tvDetailContent: TextView
    private lateinit var database: NoteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = NoteDatabase.getDatabase(this)
        tvDetailTitle = findViewById(R.id.tvDetailTitle)
        tvDetailContent = findViewById(R.id.tvDetailContent)
        val btnEdit = findViewById<Button>(R.id.btnEdit)
        val btnDelete = findViewById<Button>(R.id.btnDelete)

        noteId = intent.getIntExtra("NOTE_ID", -1)
        val title = intent.getStringExtra("NOTE_TITLE")
        val content = intent.getStringExtra("NOTE_CONTENT")

        tvDetailTitle.text = title
        tvDetailContent.text = content

        btnEdit.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            intent.putExtra("NOTE_ID", noteId)
            intent.putExtra("NOTE_TITLE", tvDetailTitle.text.toString())
            intent.putExtra("NOTE_CONTENT", tvDetailContent.text.toString())
            startActivityForResult(intent, REQUEST_CODE_EDIT_NOTE)
        }

        btnDelete.setOnClickListener {
            lifecycleScope.launch {
                val note = Note(id = noteId, title = tvDetailTitle.text.toString(), content = tvDetailContent.text.toString())
                database.noteDao().delete(note)
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT_NOTE && resultCode == RESULT_OK) {
            val updatedTitle = data?.getStringExtra("NOTE_TITLE") ?: ""
            val updatedContent = data?.getStringExtra("NOTE_CONTENT") ?: ""
            
            lifecycleScope.launch {
                val updatedNote = Note(id = noteId, title = updatedTitle, content = updatedContent)
                database.noteDao().update(updatedNote)
                
                tvDetailTitle.text = updatedTitle
                tvDetailContent.text = updatedContent
            }
        }
    }

    companion object {
        const val REQUEST_CODE_EDIT_NOTE = 2
    }
}

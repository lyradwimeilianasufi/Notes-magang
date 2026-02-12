package com.example.notes.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.notes.R
import com.example.notes.data.Note
import com.example.notes.viewmodel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailActivity : AppCompatActivity() {
    private var noteId: Int = -1
    private var isFavorite: Boolean = false
    private var timestamp: Long = 0
    private lateinit var tvDetailTitle: TextView
    private lateinit var tvDetailContent: TextView
    private lateinit var tvDetailDate: TextView
    private lateinit var btnDetailFavorite: ImageButton
    private val viewModel: NoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvDetailTitle = findViewById(R.id.tvDetailTitle)
        tvDetailContent = findViewById(R.id.tvDetailContent)
        tvDetailDate = findViewById(R.id.tvDetailDate)
        btnDetailFavorite = findViewById(R.id.btnDetailFavorite)
        val btnEdit = findViewById<Button>(R.id.btnEdit)
        val btnDelete = findViewById<Button>(R.id.btnDelete)

        noteId = intent.getIntExtra("NOTE_ID", -1)
        val title = intent.getStringExtra("NOTE_TITLE") ?: ""
        val content = intent.getStringExtra("NOTE_CONTENT") ?: ""
        isFavorite = intent.getBooleanExtra("NOTE_FAVORITE", false)
        timestamp = intent.getLongExtra("NOTE_TIMESTAMP", System.currentTimeMillis())

        tvDetailTitle.text = title
        tvDetailContent.text = content
        
        // Format and display the date
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        val dateString = sdf.format(Date(timestamp))
        tvDetailDate.text = "🗓 $dateString"
        
        updateFavoriteIcon()

        btnDetailFavorite.setOnClickListener {
            isFavorite = !isFavorite
            updateFavoriteIcon()
            val note = Note(id = noteId, title = tvDetailTitle.text.toString(), content = tvDetailContent.text.toString(), isFavorite = isFavorite, timestamp = timestamp)
            viewModel.update(note)
        }

        btnEdit.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            intent.putExtra("NOTE_ID", noteId)
            intent.putExtra("NOTE_TITLE", tvDetailTitle.text.toString())
            intent.putExtra("NOTE_CONTENT", tvDetailContent.text.toString())
            intent.putExtra("NOTE_FAVORITE", isFavorite)
            intent.putExtra("NOTE_TIMESTAMP", timestamp)
            startActivityForResult(intent, REQUEST_CODE_EDIT_NOTE)
        }

        btnDelete.setOnClickListener {
            val note = Note(id = noteId, title = tvDetailTitle.text.toString(), content = tvDetailContent.text.toString(), isFavorite = isFavorite, timestamp = timestamp)
            viewModel.delete(note)
            finish()
        }
    }

    private fun updateFavoriteIcon() {
        if (isFavorite) {
            btnDetailFavorite.setImageResource(android.R.drawable.btn_star_big_on)
        } else {
            btnDetailFavorite.setImageResource(android.R.drawable.btn_star_big_off)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT_NOTE && resultCode == RESULT_OK) {
            val updatedTitle = data?.getStringExtra("NOTE_TITLE") ?: ""
            val updatedContent = data?.getStringExtra("NOTE_CONTENT") ?: ""
            val updatedFavorite = data?.getBooleanExtra("NOTE_FAVORITE", false) ?: false
            
            // Keep the original timestamp unless you want to update it to "Last Edited"
            val updatedNote = Note(id = noteId, title = updatedTitle, content = updatedContent, isFavorite = updatedFavorite, timestamp = timestamp)
            viewModel.update(updatedNote)
            
            tvDetailTitle.text = updatedTitle
            tvDetailContent.text = updatedContent
            isFavorite = updatedFavorite
            updateFavoriteIcon()
        }
    }

    companion object {
        const val REQUEST_CODE_EDIT_NOTE = 2
    }
}

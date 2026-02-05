package com.example.notes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddNoteActivity : AppCompatActivity() {
    private var noteId: Int = -1
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_note)

        val root = findViewById<android.view.View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvHeader = findViewById<TextView>(R.id.textView)
        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etContent = findViewById<EditText>(R.id.etContent)
        val btnFavorite = findViewById<ImageButton>(R.id.btnFavorite)
        val buttonSave = findViewById<Button>(R.id.btnSave)

        // Check if we are editing an existing note
        if (intent.hasExtra("NOTE_ID")) {
            noteId = intent.getIntExtra("NOTE_ID", -1)
            etTitle.setText(intent.getStringExtra("NOTE_TITLE"))
            etContent.setText(intent.getStringExtra("NOTE_CONTENT"))
            isFavorite = intent.getBooleanExtra("NOTE_FAVORITE", false)
            buttonSave.text = "Update Catatan"
            
            updateFavoriteIcon(btnFavorite)
        }

        btnFavorite.setOnClickListener {
            isFavorite = !isFavorite
            updateFavoriteIcon(btnFavorite)
        }

        buttonSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val content = etContent.text.toString().trim()
            
            if (title.isNotEmpty() && content.isNotEmpty()) {
                val resultIntent = Intent()
                resultIntent.putExtra("NOTE_ID", noteId)
                resultIntent.putExtra("NOTE_TITLE", title)
                resultIntent.putExtra("NOTE_CONTENT", content)
                resultIntent.putExtra("NOTE_FAVORITE", isFavorite)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun updateFavoriteIcon(btnFavorite: ImageButton) {
        if (isFavorite) {
            btnFavorite.setImageResource(android.R.drawable.btn_star_big_on)
        } else {
            btnFavorite.setImageResource(android.R.drawable.btn_star_big_off)
        }
    }
}

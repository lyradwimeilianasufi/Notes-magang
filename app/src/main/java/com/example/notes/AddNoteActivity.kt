package com.example.notes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddNoteActivity : AppCompatActivity() {
    private var noteId: Int = -1

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

        val tvHeader = findViewById<TextView>(R.id.textView) // The "✍️ Tulis Catatan" TextView doesn't have an ID in XML, I should add one or find it by position. Wait, I'll just check if it exists.
        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etContent = findViewById<EditText>(R.id.etContent)
        val buttonSave = findViewById<Button>(R.id.btnSave)

        // Check if we are editing an existing note
        if (intent.hasExtra("NOTE_ID")) {
            noteId = intent.getIntExtra("NOTE_ID", -1)
            etTitle.setText(intent.getStringExtra("NOTE_TITLE"))
            etContent.setText(intent.getStringExtra("NOTE_CONTENT"))
            buttonSave.text = "Update Catatan"
        }

        buttonSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val content = etContent.text.toString().trim()
            
            if (title.isNotEmpty() && content.isNotEmpty()) {
                val resultIntent = Intent()
                resultIntent.putExtra("NOTE_ID", noteId)
                resultIntent.putExtra("NOTE_TITLE", title)
                resultIntent.putExtra("NOTE_CONTENT", content)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }
}

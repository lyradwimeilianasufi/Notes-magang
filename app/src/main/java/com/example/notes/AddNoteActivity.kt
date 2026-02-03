package com.example.notes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddNoteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_note)
        
        // Use the correct ID for the root view if it exists, 
        // otherwise we might need to find it by ID or skip this part
        // The layout activity_add_note.xml uses LinearLayout without an ID for the root.
        // Let's check if we can add an ID to the layout or just remove this padding logic.
        
        val editText = findViewById<EditText>(R.id.etContent)
        val buttonAdd = findViewById<Button>(R.id.btnSave)

        buttonAdd.setOnClickListener {
            val newNote = editText.text.toString().trim { it <= ' ' }
            if (newNote.isNotEmpty()) {
                val resultIntent = Intent()
                resultIntent.putExtra("NEW_NOTE", newNote)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }
}
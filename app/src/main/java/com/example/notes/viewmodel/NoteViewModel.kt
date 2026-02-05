package com.example.notes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.notes.data.Note
import com.example.notes.data.NoteDatabase
import com.example.notes.data.NoteRepository
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository
    private val _allNotes = MutableLiveData<List<Note>>()
    val allNotes: LiveData<List<Note>> = _allNotes

    init {
        val dao = NoteDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(dao)
        loadNotes()
    }

    fun loadNotes() {
        viewModelScope.launch {
            val notes = repository.getAllNotes()
            _allNotes.postValue(notes)
        }
    }

    fun insert(note: Note) {
        viewModelScope.launch {
            repository.insert(note)
            loadNotes()
        }
    }

    fun update(note: Note) {
        viewModelScope.launch {
            repository.update(note)
            loadNotes()
        }
    }

    fun delete(note: Note) {
        viewModelScope.launch {
            repository.delete(note)
            loadNotes()
        }
    }
}

package com.example.notes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.notes.data.BackupWorker
import com.example.notes.data.Note
import com.example.notes.data.NoteDatabase
import com.example.notes.data.NoteRepository
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository
    private val _allNotes = MutableLiveData<List<Note>>()
    val allNotes: LiveData<List<Note>> = _allNotes

    init {
        val dao = NoteDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(dao)
        loadNotes()
        scheduleBackup()
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

    private fun scheduleBackup() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val backupRequest = PeriodicWorkRequestBuilder<BackupWorker>(15, TimeUnit.MINUTES) // Minimal 15 menit untuk berkala
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(getApplication()).enqueue(backupRequest)
    }
}

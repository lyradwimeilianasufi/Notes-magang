package com.example.notes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
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
    val allNotes: LiveData<List<Note>>

    init {
        val dao = NoteDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(dao)
        allNotes = repository.allNotes
        scheduleBackup()
    }

    fun insert(note: Note) {
        viewModelScope.launch {
            repository.insert(note)
        }
    }

    fun update(note: Note) {
        viewModelScope.launch {
            repository.update(note)
        }
    }

    fun delete(note: Note) {
        viewModelScope.launch {
            repository.delete(note)
        }
    }

    private fun scheduleBackup() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val backupRequest = PeriodicWorkRequestBuilder<BackupWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(getApplication()).enqueue(backupRequest)
    }
}

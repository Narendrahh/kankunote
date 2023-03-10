package com.example.notekanku.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.notekanku.Database.NoteDatabase
import com.example.notekanku.Database.NotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository : NotesRepository

    val allnotes : LiveData<List<Note>>

    init {
        val dao = NoteDatabase.getDatabase(application).getNoteDao()
        repository = NotesRepository(dao)
        allnotes = repository.allNotes

    }

    fun deleteNote(note : Note) = viewModelScope.launch(Dispatchers.IO) {

        repository.delete(note)

    }

    fun insertnote(note: Note) = viewModelScope.launch(Dispatchers.IO) {

        repository.insert(note)

    }

    fun updateNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {

        repository.update(note)

    }




}
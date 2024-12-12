package com.secure.Notes.Service;

import com.secure.Notes.Model.Note;
import com.secure.Notes.Repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteServiceImp implements NoteService{


    NoteRepository noteRepository;

    public NoteServiceImp(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    public Note createNoteForUser(String username, String content) {
        Note note = new Note();
        note.setContent(content);
        note.setOwnerUserName(username);
        Note savedNote = noteRepository.save(note);
        return savedNote;
    }

    @Override
    public Note updateNoteForUSer(Long noteId, String content, String username) {
        Note note = noteRepository.findById(noteId).orElseThrow(()->new RuntimeException("Note not found"));
        note.setContent(content);
        Note updateNote = noteRepository.save(note);
        return updateNote;
    }

    @Override
    public Boolean deleteNoteForUser(Long noteId, String username) {
        if(noteRepository.existsById(noteId)) {
            noteRepository.deleteById(noteId);
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public List<Note> getNotesForUser(String username) {
        List<Note> personalNotes = noteRepository.findByOwnerUserName(username);
        return personalNotes;
    }
}

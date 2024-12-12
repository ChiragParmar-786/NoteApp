package com.secure.Notes.Repository;

import com.secure.Notes.Model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note,Long> {
    List<Note> findByOwnerUserName(String ownusername);
}

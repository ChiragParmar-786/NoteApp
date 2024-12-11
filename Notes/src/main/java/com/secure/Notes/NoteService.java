package com.secure.Notes;

import java.util.List;

public interface NoteService {

    Note createNoteForUser(String username,String content);

    Note updateNoteForUSer(Long noteId, String content,String username);

    Boolean deleteNoteForUser(Long noteId, String username);

    List<Note> getNotesForUser(String username);


}

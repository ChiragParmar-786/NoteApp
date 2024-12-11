package com.secure.Notes;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public ResponseEntity<List<Note>> getUserNotes(@AuthenticationPrincipal UserDetails userDetails){
        List<Note> notes = noteService.getNotesForUser(userDetails.getUsername());
        return new ResponseEntity<>(notes, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody String content, @AuthenticationPrincipal UserDetails userDetails){
        Note note = noteService.createNoteForUser(userDetails.getUsername(),content);
        if(note != null)
            return new ResponseEntity<>(note,HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
   }

   @PutMapping("/{noteId}")
   public ResponseEntity<Note> updateNote(@PathVariable Long noteId,@RequestBody String content,@AuthenticationPrincipal UserDetails userDetails){
        Note note = noteService.updateNoteForUSer(noteId,content,userDetails.getUsername());
       if(note != null)
           return new ResponseEntity<>(note,HttpStatus.OK);
       else
           return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
   }

   @DeleteMapping("/{noteId}")
   public ResponseEntity<String> deleteNote(@PathVariable Long noteId,@AuthenticationPrincipal UserDetails userDetails){

        Boolean sts = noteService.deleteNoteForUser(noteId,userDetails.getUsername());
        if(sts)
            return new ResponseEntity<>("Note is Deleted",HttpStatus.OK);
        else
            return new ResponseEntity<>("Note not found for given id",HttpStatus.NOT_FOUND);
   }

}

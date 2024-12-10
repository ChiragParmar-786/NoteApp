package com.secure.Notes;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity
public class Note {

     @Id
     @GeneratedValue
     private Long id;
     @Lob
     private String content;
     private String ownerUserName;

    public Note() {
    }

    public Note(Long id, String content, String ownerUserName) {
        this.id = id;
        this.content = content;
        this.ownerUserName = ownerUserName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOwnerUserName() {
        return ownerUserName;
    }

    public void setOwnerUserName(String ownerUserName) {
        this.ownerUserName = ownerUserName;
    }
}

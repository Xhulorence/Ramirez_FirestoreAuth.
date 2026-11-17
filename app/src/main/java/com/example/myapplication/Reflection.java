package com.example.myapplication;

public class Reflection {
    private String reflectionId;
    private String title;
    private String bibleVerse;
    private String reflectionNotes;
    private long createdAt;
    private long updatedAt;

    public Reflection() {
        // Public no-arg constructor required for Firestore deserialization
    }

    public Reflection(String reflectionId, String title, String bibleVerse, String reflectionNotes, long createdAt, long updatedAt) {
        this.reflectionId = reflectionId;
        this.title = title;
        this.bibleVerse = bibleVerse;
        this.reflectionNotes = reflectionNotes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getReflectionId() {
        return reflectionId;
    }

    public void setReflectionId(String reflectionId) {
        this.reflectionId = reflectionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBibleVerse() {
        return bibleVerse;
    }

    public void setBibleVerse(String bibleVerse) {
        this.bibleVerse = bibleVerse;
    }

    public String getReflectionNotes() {
        return reflectionNotes;
    }

    public void setReflectionNotes(String reflectionNotes) {
        this.reflectionNotes = reflectionNotes;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}

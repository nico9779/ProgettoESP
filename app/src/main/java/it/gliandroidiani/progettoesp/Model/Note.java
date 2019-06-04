package it.gliandroidiani.progettoesp.Model;

public class Note {
    private String noteText;
    private long noteData;

    public Note(String noteText, long noteData) {
        this.noteText = noteText;
        this.noteData = noteData;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public long getNoteData() {
        return noteData;
    }

    public void setNoteData(long noteData) {
        this.noteData = noteData;
    }
}

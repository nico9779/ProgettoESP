package it.gliandroidiani.progettoesp.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import it.gliandroidiani.progettoesp.Model.Note;
import it.gliandroidiani.progettoesp.R;

public class NotesAdapter  extends RecyclerView.Adapter<NotesAdapter.NoteHolder>
{
    private ArrayList<Note> notes;

    public NotesAdapter(ArrayList<Note> notes) {
        this.notes = notes;
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext().inflate(R.layout.note_layout,parent,false);
        return new NoteHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder noteHolder, int position) {

        Note note = getNote(position);
        if(note!= null){
            noteHolder.noteText.setText(notes.getNoteText());
            noteHolder.noteData.setText();

         }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    private Note getNote(int position){

        return notes.get(position);
    }

    class NoteHolder extends RecyclerView.ViewHolder{

        TextView noteText, noteData;
        public NoteHolder(@NonNull View itemView) {
            super(itemView);
        }
    }



}
package it.gliandroidiani.progettoesp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/*
Questa classe rappresenta l'adapter del RecyclerView che mostra le note nella sezione "Note"
della main activity
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> notes = new ArrayList<>();
    private OnItemClickListener listener;


    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.note_item, viewGroup, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i) {
        //Seleziono la nota corrente e imposto le varie textview in base alle sue proprietà
        Note currentNote = notes.get(i);
        noteViewHolder.title.setText(currentNote.getTitle());
        noteViewHolder.description.setText(currentNote.getDescription());
        noteViewHolder.data.setText(currentNote.getDateTimeFormatted(noteViewHolder.data.getContext()));
    }

    //Metodo che mi restituisce il numero di note nel RecyclerView
    @Override
    public int getItemCount() {
        return notes.size();
    }

    /*
    Metodo che notifica all'adapter che il DataSet è stato modificato in seguito all'aggiunta o
    alla rimozione o alla modifica di note e che deve essere aggiornata l'interfaccia utente
     */
    void setNotes(List<Note> notes){
        this.notes = notes;
        notifyDataSetChanged();
    }

    //Metodo che restituisce una nota in base alla sua posizione
    Note getNoteAt(int position){
        return notes.get(position);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder{

        /*
        Variabili private del ViewHolder che rappresentano gli elementi del layout
        dell'elemento nota presente in note_item che poi viene inserito nel RecyclerView
        */
        private TextView title;
        private TextView description;
        private TextView data;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            //Inizializzazione
            title = itemView.findViewById(R.id.note_title_item);
            description = itemView.findViewById(R.id.note_description_item);
            data = itemView.findViewById(R.id.list_note_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION)
                        listener.onItemClick(notes.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Note note);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}

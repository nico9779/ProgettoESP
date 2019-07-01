package it.gliandroidiani.progettoesp;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


/*
Questa classe rappresenta il fragment che visualizza tutte le note ed è ospitato dalla main activity
 */
public class NoteFragment extends Fragment {

    /*
    Stringhe utilizzate dall'intent per aggiungere gli extra da trasmettere all'activity AddEditNote
    nel caso in cui venga modificata una sveglia
    */
    public static final int EDIT_NOTE_REQUEST = 4;
    public static final String EXTRA_ID_NOTE = "it.gliandroidiani.progettoesp.EXTRA_ID_NOTE";
    public static final String EXTRA_TITLE_NOTE = "it.gliandroidiani.progettoesp.EXTRA_TITLE_NOTE";
    public static final String EXTRA_DESCRIPTION_NOTE = "it.gliandroidiani.progettoesp.EXTRA_DESCRIPTION_NOTE";

    //Variabili private
    private NoteViewModel noteViewModel;
    private TextView noNoteTextView;


    public NoteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_note, container, false);

        noNoteTextView = view.findViewById(R.id.starting_string_note);
        //Imposto la toolbar e indico la presenza del menu
        Toolbar noteToolbar = view.findViewById(R.id.note_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(noteToolbar);
        setHasOptionsMenu(true);

        //Dichiaro il recyclerView e imposto l'adapter
        RecyclerView recyclerView = view.findViewById(R.id.note_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        final NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        /*
        Istanzio il viewmodel e imposto sulle note del database un observer.
        In questo modo ogni volta che si verifica un cambiamento all'interno del database
        come un'inserimento, una modifica o una rimozione viene chiamato il metodo onChanged
        che passa all'adapter come parametro la lista delle note del database aggiornata
        e le imposta nel recyclerview.
        Inoltre nel caso in cui non sia presente alcuna nota viene mostrata una textview per
        indicare l'assenza di note.
         */
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(@Nullable List<Note> notes) {
                adapter.setNotes(notes);

                if(notes.isEmpty()){
                    noNoteTextView.setVisibility(View.VISIBLE);
                }
                else {
                    noNoteTextView.setVisibility(View.GONE);
                }
            }
        });

        /*
        Aggiungo un ItemTouchHelper che vado a connettere al recyclerview sovrascrivendo il metodo
        onSwiped in modo tale che ogni volta che scorro una nota verso destra o verso sinistra
        si apre una finestra che conferma la cancellazione di una nota
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
                //Creo la finestra per accettare la cancellazione della nota
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
                builder.setTitle(getResources().getString(R.string.delete_note));
                builder.setMessage(getResources().getString(R.string.message_delete_note));
                builder.setCancelable(false);
                builder.setPositiveButton(getResources().getString(R.string.ok_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Elimino la nota dal database
                        Note note = adapter.getNoteAt(viewHolder.getAdapterPosition());
                        noteViewModel.deleteNote(note);
                        Toast.makeText(getActivity(), R.string.event_delete_note, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNeutralButton(getResources().getString(R.string.cancel_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*
                        Nel caso in cui annullo la cancellazione notifico che l'oggetto per
                        cui avevo fatto lo swipe è stato modificato altrimenti non sarabbe
                        più visibile nel recyclerview
                         */

                        adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }).attachToRecyclerView(recyclerView);

        /*
        Imposto il listener sull'adapter e sovrascrivo il metodo onItemClick dell'interfaccia
        per modificare una nota.
         */
        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                /*
                Creo un'intent per chiamare l'activity AddEditNote e passo nell'intent tutti
                i parametri della nota
                 */
                Intent intent = new Intent(getActivity(), ActivityAddEditNote.class);
                intent.putExtra(EXTRA_ID_NOTE, note.getId());
                intent.putExtra(EXTRA_TITLE_NOTE, note.getTitle());
                intent.putExtra(EXTRA_DESCRIPTION_NOTE, note.getDescription());
                startActivityForResult(intent, EDIT_NOTE_REQUEST);
            }
        });

        return view;
    }

    //Metodo che aggiunge il menu alla toolbar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_note_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /* Metodo che mi permette di stabilire cosa fare quando clicco
    su un'icona del menu della toolbar */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_all_notes) {
            List<Note> notes = noteViewModel.getAllNotes().getValue();
            /*
            Nel caso in cui ci siano delle note salvate nel database creo una finestra di
            conferma di cancellazione di tutte le note
             */
            if(!notes.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
                builder.setTitle(getResources().getString(R.string.delete_notes));
                builder.setMessage(getResources().getString(R.string.message_delete_all_notes));
                builder.setCancelable(false);
                builder.setPositiveButton(getResources().getString(R.string.ok_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Nel caso in cui premo "OK" cancello tutte le note dal database
                        noteViewModel.deleteAllNotes();
                        Toast.makeText(getActivity(), R.string.deleted_all_notes, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNeutralButton(getResources().getString(R.string.cancel_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
            //Altrimenti mostro un toast che dichiara l'assenza di note nel database
            else {
                Toast.makeText(getActivity(), R.string.no_notes_to_delete, Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

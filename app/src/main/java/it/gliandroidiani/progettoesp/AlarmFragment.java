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



/**
 * A simple {@link Fragment} subclass.
 */
public class AlarmFragment extends Fragment {

    /*
    Stringhe utilizzate dall'intent per aggiungere gli extra da trasmettere all'activity AddEditAlarm
    nel caso in cui venga modificata una sveglia
    */
    public static final int EDIT_ALARM_REQUEST = 2;
    public static final String EXTRA_ID = "it.gliandroidiani.progettoesp.EXTRA_ID";
    public static final String EXTRA_TITLE = "it.gliandroidiani.progettoesp.EXTRA_TITLE";
    public static final String EXTRA_HOURS = "it.gliandroidiani.progettoesp.EXTRA_HOURS";
    public static final String EXTRA_MINUTE = "it.gliandroidiani.progettoesp.EXTRA_MINUTE";
    public static final String EXTRA_RINGTONE = "it.gliandroidiani.progettoesp.EXTRA_RINGTONE";
    public static final String EXTRA_VIBRATION = "it.gliandroidiani.progettoesp.EXTRA_VIBRATION";
    public static final String EXTRA_ACTIVE = "it.gliandroidiani.progettoesp.EXTRA_ACTIVE";
    public static final String EXTRA_REPETITION_TYPE = "it.gliandroidiani.progettoesp.EXTRA_REPETITION_TYPE";
    public static final String EXTRA_REPETITION_DAYS = "it.gliandroidiani.progettoesp.EXTRA_REPETITION_DAYS";

    //Variabili private
    private AlarmViewModel alarmViewModel;
    private TextView noAlarmTextView;

    public AlarmFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        noAlarmTextView = view.findViewById(R.id.starting_string_alarm);
        //Imposto la toolbar e indico la presenza del menu
        Toolbar alarmToolbar = view.findViewById(R.id.alarm_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(alarmToolbar);
        setHasOptionsMenu(true);

        //Dichiaro il recyclerView e imposto l'adapter
        RecyclerView recyclerView = view.findViewById(R.id.alarm_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        final AlarmAdapter adapter = new AlarmAdapter();
        recyclerView.setAdapter(adapter);

        alarmViewModel = ViewModelProviders.of(this).get(AlarmViewModel.class);
        alarmViewModel.getAllAlarms().observe(this, new Observer<List<Alarm>>() {
            @Override
            public void onChanged(@Nullable List<Alarm> alarms) {
                adapter.setAlarms(alarms);

                if(alarms.size() == 0) {
                    noAlarmTextView.setVisibility(View.VISIBLE);
                }
                else {
                    noAlarmTextView.setVisibility(View.GONE);
                }
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
                builder.setTitle(getResources().getString(R.string.delete_alarm));
                builder.setMessage(getResources().getString(R.string.message_delete_alarm));
                builder.setCancelable(false);
                builder.setPositiveButton(getResources().getString(R.string.ok_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Alarm alarm = adapter.getAlarmAt(viewHolder.getAdapterPosition());
                        alarmViewModel.deleteAlarm(alarm);
                        ScheduleAlarmHelper.cancelAlarm(getActivity(), alarm);
                        Toast.makeText(getActivity(), R.string.event_delete_alarm, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNeutralButton(getResources().getString(R.string.cancel_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new AlarmAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Alarm alarm) {
                Intent intent = new Intent(getActivity(), ActivityAddEditAlarm.class);
                intent.putExtra(EXTRA_ID, alarm.getId());
                intent.putExtra(EXTRA_TITLE, alarm.getTitle());
                intent.putExtra(EXTRA_HOURS, alarm.getHours());
                intent.putExtra(EXTRA_MINUTE, alarm.getMinute());
                intent.putExtra(EXTRA_RINGTONE, alarm.isRingtone());
                intent.putExtra(EXTRA_VIBRATION, alarm.isVibration());
                intent.putExtra(EXTRA_ACTIVE, alarm.isActive());
                intent.putExtra(EXTRA_REPETITION_TYPE, alarm.getRepetitionType());
                intent.putExtra(EXTRA_REPETITION_DAYS, alarm.getRepetitionDays());
                startActivityForResult(intent, EDIT_ALARM_REQUEST);
            }

            @Override
            public void onImageClick(Alarm alarm) {
                if(alarm.isActive()){
                    alarm.setActive(false);
                    alarmViewModel.updateAlarm(alarm);
                    ScheduleAlarmHelper.cancelAlarm(getActivity(), alarm);
                    Toast.makeText(getActivity(), R.string.inactive_alarm, Toast.LENGTH_SHORT).show();
                }
                else {
                    alarm.setActive(true);
                    alarmViewModel.updateAlarm(alarm);
                    ScheduleAlarmHelper.scheduleAlarm(getActivity(), alarm.getId(),alarm);
                    Toast.makeText(getActivity(), R.string.active_alarm, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    //Metodo che aggiunge il menu alla toolbar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_alarm_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /* Metodo che mi permette di stabilire cosa fare quando clicco
    su un'icona del menu della toolbar */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_all_alarms) {
            List<Alarm> alarms = alarmViewModel.getAllAlarms().getValue();
            /*
            Nel caso in cui ci siano delle sveglie salvate nel database creo una finestra di
            conferma di cancellazione di tutte le sveglie e disattivo gli allarmi
             */
            if(!alarms.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
                builder.setTitle(getResources().getString(R.string.delete_alarms));
                builder.setMessage(getResources().getString(R.string.message_delete_all_alarms));
                builder.setCancelable(false);
                builder.setPositiveButton(getResources().getString(R.string.ok_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alarmViewModel.deleteAllAlarms();
                        ScheduleAlarmHelper.cancelAllAlarm(getActivity(), alarmViewModel);
                        Toast.makeText(getActivity(), R.string.deleted_all_alarms, Toast.LENGTH_SHORT).show();
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
            //Altrimenti mostro un toast che dichiara l'assenza di sveglie nel database
            else {
                Toast.makeText(getActivity(), R.string.no_alarms_to_delete, Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

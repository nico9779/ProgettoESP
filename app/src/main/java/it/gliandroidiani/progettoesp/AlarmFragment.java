package it.gliandroidiani.progettoesp;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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

import java.util.Calendar;
import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 */
public class AlarmFragment extends Fragment {

    public static final int EDIT_ALARM_REQUEST = 2;
    public static final String EXTRA_ID = "it.gliandroidiani.progettoesp.EXTRA_ID";
    public static final String EXTRA_TITLE = "it.gliandroidiani.progettoesp.EXTRA_TITLE";
    public static final String EXTRA_HOURS = "it.gliandroidiani.progettoesp.EXTRA_HOURS";
    public static final String EXTRA_MINUTE = "it.gliandroidiani.progettoesp.EXTRA_MINUTE";
    public static final String EXTRA_RINGTONE = "it.gliandroidiani.progettoesp.EXTRA_RINGTONE";
    public static final String EXTRA_VIBRATION = "it.gliandroidiani.progettoesp.EXTRA_VIBRATION";
    public static final String EXTRA_REPETITION_TYPE = "it.gliandroidiani.progettoesp.EXTRA_REPETITION_TYPE";
    public static final String EXTRA_REPETITION_DAYS = "it.gliandroidiani.progettoesp.EXTRA_REPETITION_DAYS";

    private AlarmViewModel alarmViewModel;
    TextView noAlarmTextView;
    Toolbar alarmToolbar;

    public AlarmFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        noAlarmTextView = view.findViewById(R.id.starting_string_alarm);
        alarmToolbar = view.findViewById(R.id.alarm_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(alarmToolbar);
        setHasOptionsMenu(true);

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
                if(alarms.size() == 0)
                    noAlarmTextView.setVisibility(View.VISIBLE);
                else
                    noAlarmTextView.setVisibility(View.GONE);
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
                builder.setTitle("Elimina sveglia");
                builder.setMessage("Sei sicuro di voler eliminare questa sveglia?");
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Alarm alarm = adapter.getAlarmAt(viewHolder.getAdapterPosition());
                        alarmViewModel.deleteAlarm(alarm);
                        cancelAlarm(alarm);
                        Toast.makeText(getActivity(), R.string.event_delete_alarm, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNeutralButton("ANNULLA", new DialogInterface.OnClickListener() {
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
                intent.putExtra(EXTRA_REPETITION_TYPE, alarm.getRepetitionType());
                intent.putExtra(EXTRA_REPETITION_DAYS, alarm.getRepetitionDays());
                startActivityForResult(intent, EDIT_ALARM_REQUEST);
            }

            @Override
            public void onImageClick(Alarm alarm, int position) {
                if(alarm.isActive()){
                    alarm.setActive(false);
                    adapter.notifyItemChanged(position);
                    cancelAlarm(alarm);
                    Toast.makeText(getActivity(), "Sveglia disattivata", Toast.LENGTH_SHORT).show();
                }
                else {
                    alarm.setActive(true);
                    adapter.notifyItemChanged(position);
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY, alarm.getHours());
                    c.set(Calendar.MINUTE, alarm.getMinute());
                    c.set(Calendar.SECOND, 0);
                    startAlarm(c,alarm.getId(),alarm);
                    Toast.makeText(getActivity(), "Sveglia riattivata", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_alarm_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_all_alarms) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
            builder.setTitle("Elimina sveglie");
            builder.setMessage("Sei sicuro di voler eliminare tutte le sveglie?");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alarmViewModel.deleteAllAlarms();
                    cancelAllAlarm();
                    Toast.makeText(getActivity(), R.string.deleted_all_alarms, Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNeutralButton("ANNULLA", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startAlarm(Calendar c, long alarmID, Alarm alarm){
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlertReceiver.class);
        intent.putExtra("Title", alarm.getTitle());
        intent.putExtra("AlarmID", alarmID);
        intent.putExtra("Hours", alarm.getHours());
        intent.putExtra("Minute", alarm.getMinute());
        intent.putExtra("Ringtone", alarm.isRingtone());
        intent.putExtra("Vibration", alarm.isVibration());
        intent.putExtra("Repetition", alarm.getRepetitionType());
        String repetition = alarm.getRepetitionType();
        if(repetition.equals("Una sola volta")){
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), (int) (alarmID*6), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if(c.before(Calendar.getInstance())){
                c.add(Calendar.DATE, 1);
            }

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        }
        if(repetition.equals("Giornalmente")){
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), (int) (alarmID*6), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if(c.before(Calendar.getInstance())){
                c.add(Calendar.DATE, 1);
            }

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);
        }
        else {
            for (int i = 0; i <= 6; i++) {
                if(alarm.getRepetitionDays()[i]){
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), (int) (alarmID*6+i), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    if(i!=6) {
                        c.set(Calendar.DAY_OF_WEEK, i+2);
                    }
                    else{
                        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                    }

                    if(c.before(Calendar.getInstance())){
                        c.add(Calendar.DATE, 7);
                    }

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), (AlarmManager.INTERVAL_DAY)*7,pendingIntent);
                }
            }
        }
    }

    private void cancelAlarm(Alarm alarm) {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlertReceiver.class);

        if(alarm.getRepetitionType().equals("Una sola volta") || alarm.getRepetitionType().equals("Giornalmente")) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), (int) (alarm.getId() * 6), intent, 0);
            alarmManager.cancel(pendingIntent);
        }
        else {
            for (int i = 0; i <= 6; i++) {
                if(alarm.getRepetitionDays()[i]){
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), (int) (alarm.getId() * 6+i), intent, 0);
                    alarmManager.cancel(pendingIntent);
                }
            }
        }
    }

    private void cancelAllAlarm() {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlertReceiver.class);
        List<Alarm> alarms = alarmViewModel.getAllAlarms().getValue();
        for(Alarm alarm:alarms) {
            if(alarm.getRepetitionType().equals("Una sola volta") || alarm.getRepetitionType().equals("Giornalmente")) {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), (int) (alarm.getId() * 6), intent, 0);
                alarmManager.cancel(pendingIntent);
            }
            else {
                for (int i = 0; i <= 6; i++) {
                    if(alarm.getRepetitionDays()[i]){
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), (int) (alarm.getId() * 6+i), intent, 0);
                        alarmManager.cancel(pendingIntent);
                    }
                }
            }
        }
    }
}

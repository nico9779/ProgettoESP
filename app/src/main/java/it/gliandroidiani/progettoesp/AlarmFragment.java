package it.gliandroidiani.progettoesp;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    public static final int EDIT_ALARM_REQUEST = 2;
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
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                alarmViewModel.deleteAlarm(adapter.getAlarmAt(viewHolder.getAdapterPosition()));
                Toast.makeText(getActivity(), R.string.event_delete_alarm, Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new AlarmAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Alarm alarm) {
                Intent intent = new Intent(getActivity(), ActivityAddEditAlarm.class);
                intent.putExtra(ActivityAddEditAlarm.EXTRA_ID, alarm.getId());
                intent.putExtra(ActivityAddEditAlarm.EXTRA_TITLE, alarm.getTitle());
                intent.putExtra(ActivityAddEditAlarm.EXTRA_HOURS, alarm.getHours());
                intent.putExtra(ActivityAddEditAlarm.EXTRA_MINUTE, alarm.getMinute());
                intent.putExtra(ActivityAddEditAlarm.EXTRA_VIBRATION, alarm.isVibration());
                startActivityForResult(intent, EDIT_ALARM_REQUEST);
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
            alarmViewModel.deleteAllAlarms();
            Toast.makeText(getActivity(), R.string.deleted_all_alarms, Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
